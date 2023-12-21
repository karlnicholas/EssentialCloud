package com.example.essentialcloud.transferbroker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
@RequestMapping("/api/v1")
public class TransferBrokerController {
    private final TransferRequestService transferRequestService;
    private final WebClient internalWebClient;
    private final ObjectMapper objectMapper;

    public TransferBrokerController(TransferRequestService transferRequestService, WebClient internalWebClient, ObjectMapper objectMapper) {
        this.transferRequestService = transferRequestService;
        this.internalWebClient = internalWebClient;
        this.objectMapper = objectMapper;
    }

    @PostMapping("createTransferRequest")
    public void requestTransfer(@RequestBody TransferRequestDto transferRequestDto) {
        TransferRequest transferRequest = transferRequestService.createTransferRequest(transferRequestDto);
        internalWebClient.get().uri(
                        uriBuilder -> uriBuilder.path("/api/v1/stepTransferRequest")
                                .queryParam("transferId", transferRequest.getId())
                                .build()
                )
                .attributes(clientRegistrationId("auth0-login"))
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }

    @GetMapping("stepTransferRequest")
    public void stepTransfer(@RequestParam("transferId") Long transferId) {
        transferRequestService.stepTransfer(transferId);
    }
    @GetMapping("listTransferRequests")
    public String listTransferRequests(@RequestParam("principalName") String principalName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("authentication:" + authentication);
        }
        try {
            List<TransferRequestDto> transferRequests = transferRequestService.listTransferRequests(principalName)
                    .stream()
                    .map(transferRequest -> TransferRequestDto.builder()
                            .sourceRequestAccount(transferRequest.getSourceRequestAccount())
                            .targetRequestAccount(transferRequest.getTargetRequestAccount())
                            .amount(transferRequest.getAmount())
                            .build())
                    .toList();
            return objectMapper.writeValueAsString(transferRequests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("allTransferRequests")
    @PreAuthorize("hasAuthority('SCOPE_transferbroker')")
    public String allTransferRequests() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            System.out.println("authentication:" + authentication);
        }
        try {
            List<TransferRequestDto> transferRequests = transferRequestService.allTransferRequests()
                    .stream()
                    .map(transferRequest -> TransferRequestDto.builder()
                            .principalName(transferRequest.getPrincipalName())
                            .sourceRequestAccount(transferRequest.getSourceRequestAccount())
                            .targetRequestAccount(transferRequest.getTargetRequestAccount())
                            .amount(transferRequest.getAmount())
                            .build())
                    .toList();
            return objectMapper.writeValueAsString(transferRequests);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
