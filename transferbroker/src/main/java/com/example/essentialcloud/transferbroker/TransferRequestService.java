package com.example.essentialcloud.transferbroker;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Service
public class TransferRequestService {
    private final TranferRequestRepository tranferRequestRepository;
    private final BrokerAccountRepository brokerAccountRepository;
    private final WebClient checkingAccountWebClient;
    private final WebClient savingAccountWebClient;
    private final WebClient internalWebClient;

    public TransferRequestService(TranferRequestRepository tranferRequestRepository, BrokerAccountRepository brokerAccountRepository, WebClient checkingAccountWebClient, WebClient savingAccountWebClient, WebClient internalWebClient) {
        this.tranferRequestRepository = tranferRequestRepository;
        this.brokerAccountRepository = brokerAccountRepository;
        this.checkingAccountWebClient = checkingAccountWebClient;
        this.savingAccountWebClient = savingAccountWebClient;
        this.internalWebClient = internalWebClient;
    }


    public TransferRequest createTransferRequest(TransferRequestDto transferRequestDto) {
        return tranferRequestRepository.save(TransferRequest.builder()
                .sourceRequestAccount(transferRequestDto.getSourceRequestAccount())
                .targetRequestAccount(transferRequestDto.getTargetRequestAccount())
                .principalName(transferRequestDto.getPrincipalName())
                .amount(transferRequestDto.getAmount())
                .stepState(StaticStepEnum.TRANSFER_TO_BROKER)
                .build()
        );
    }

    /**
     * Home of business logic for handling transfers
     *
     * @param transferId Transfer Id
     */
    public void stepTransfer(Long transferId) {
        tranferRequestRepository.findById(transferId)
                .ifPresent(this::handleStepTransfer);
    }

    private void handleStepTransfer(TransferRequest transferRequest) {
        switch (transferRequest.getStepState()) {
            case TRANSFER_TO_BROKER -> {
                WebClient webClient = transferRequest.getSourceRequestAccount().compareToIgnoreCase("checking") == 0 ? checkingAccountWebClient : savingAccountWebClient;
                Long transferId = webClient.get().uri(builder -> builder
                                .path("/api/v1/withdraw")
                                .queryParam("principalName", transferRequest.getPrincipalName())
                                .queryParam("amount", transferRequest.getAmount())
                                .build())
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve()
                        .bodyToMono(Long.class)
                        .block();
                brokerAccountRepository.findById(1L)
                        .ifPresentOrElse(brokerAccount -> {
                            brokerAccount.setBalance(brokerAccount.getBalance().add(new BigDecimal(transferRequest.getAmount())));
                            brokerAccountRepository.save(brokerAccount);
                        }, () -> {
                            throw new RuntimeException("broker account not found");
                        });
                sendNextStep(transferRequest, transferId);
            }
            case VERIFY_TRANSFER_FROM_SUCCESS -> {
                WebClient webClient = transferRequest.getSourceRequestAccount().compareToIgnoreCase("checking") == 0 ? checkingAccountWebClient : savingAccountWebClient;
                webClient.get().uri(builder -> builder
                                .path("/api/v1/verifyTransfer")
                                .queryParam("transferId", transferRequest.getTransferId())
                                .build())
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve()
                        .bodyToMono(Long.class)
                        .block();
                sendNextStep(transferRequest, -1L);
            }
            case TRANSFER_TO_ACCOUNT -> {
                WebClient webClient = transferRequest.getTargetRequestAccount().compareToIgnoreCase("checking") == 0 ? checkingAccountWebClient : savingAccountWebClient;
                Long transferId = webClient.get().uri(builder -> builder
                                .path("/api/v1/deposit")
                                .queryParam("principalName", transferRequest.getPrincipalName())
                                .queryParam("amount", transferRequest.getAmount())
                                .build())
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve()
                        .bodyToMono(Long.class)
                        .block();
                brokerAccountRepository.findById(1L)
                        .ifPresentOrElse(brokerAccount -> {
                            brokerAccount.setBalance(brokerAccount.getBalance().subtract(new BigDecimal(transferRequest.getAmount())));
                            brokerAccountRepository.save(brokerAccount);
                        }, () -> {
                            throw new RuntimeException("broker account not found");
                        });
                sendNextStep(transferRequest, transferId);
            }
            case VERIFY_TRANSFER_TO_SUCCESS -> {
                WebClient webClient = transferRequest.getTargetRequestAccount().compareToIgnoreCase("checking") == 0 ? checkingAccountWebClient : savingAccountWebClient;
                webClient.get().uri(builder -> builder
                                .path("/api/v1/verifyTransfer")
                                .queryParam("transferId", transferRequest.getTransferId())
                                .build())
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve()
                        .bodyToMono(Long.class)
                        .block();
                sendNextStep(transferRequest, -1L);
            }
            case TRANSFER_COMPLETE -> sendNextStep(transferRequest, -1L);
        }
    }

    private void sendNextStep(TransferRequest transferRequest, Long transferId) {
        tranferRequestRepository.findById(transferRequest.getId())
                .ifPresent(transferRequest1 -> {
                    transferRequest1.setStepState(nextStaticStep(transferRequest1));
                    transferRequest1.setTransferId(transferId);
                    tranferRequestRepository.save(transferRequest1);
                    if (transferRequest1.getStepState().compareTo(StaticStepEnum.TRANSFER_COMPLETE) != 0) {
                        internalWebClient.get().uri(uriBuilder ->
                                        uriBuilder.path("/api/v1/stepTransferRequest")
                                                .queryParam("transferId", transferRequest.getId())
                                                .build()
                                ).attributes(clientRegistrationId("auth0-login"))
                                .retrieve()
                                .bodyToMono(Void.class)
                                .subscribe();
                    }
                });
    }

    private StaticStepEnum nextStaticStep(TransferRequest transferRequest) {
        return switch (transferRequest.getStepState()) {
            case TRANSFER_TO_BROKER -> StaticStepEnum.VERIFY_TRANSFER_FROM_SUCCESS;
            case VERIFY_TRANSFER_FROM_SUCCESS -> StaticStepEnum.TRANSFER_TO_ACCOUNT;
            case TRANSFER_TO_ACCOUNT -> StaticStepEnum.VERIFY_TRANSFER_TO_SUCCESS;
            case VERIFY_TRANSFER_TO_SUCCESS, TRANSFER_COMPLETE -> StaticStepEnum.TRANSFER_COMPLETE;
        };
    }

    public List<TransferRequest> listTransferRequests(String principalName) {
        return tranferRequestRepository.findAllByPrincipalName(principalName);
    }

    public List<TransferRequest> allTransferRequests() {
        return tranferRequestRepository.findAll();
    }
}
