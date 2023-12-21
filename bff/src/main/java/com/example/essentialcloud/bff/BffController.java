package com.example.essentialcloud.bff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Controller
@Slf4j
public class BffController {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BffController(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/")
    public String home(Model model) {
        return setIndexModel(model);
    }

    @GetMapping("/transfers")
    public String transfers(Model model) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("profile", authentication.getPrincipal());
            String transfers = webClient.get()
                    .uri(uriBuilder -> uriBuilder.scheme("http").host("localhost").port(8120).path("/api/v1/listTransferRequests").queryParam("principalName", authentication.getName()).build())
                    .attributes(clientRegistrationId("auth0-login"))
                    .retrieve().bodyToMono(String.class).block();
            List<TransferModel> transferList = objectMapper.readValue(transfers, new TypeReference<>() {});
            model.addAttribute("transfers", transferList);
        }
        return "transfers";
    }

    private String setIndexModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("profile", authentication.getPrincipal());
            try {
                Mono<String> checkingBalance = webClient
                        .get()
                        .uri("http://localhost:8090/api/v1/account")
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve().bodyToMono(String.class);
                Mono<String> savingBalance = webClient
                        .get()
                        .uri("http://localhost:8110/api/v1/account")
                        .attributes(clientRegistrationId("auth0-login"))
                        .retrieve().bodyToMono(String.class);
                Tuple2<String, String> balances = Mono.zip(checkingBalance, savingBalance).block();
                model.addAttribute("checkingBalance", balances.getT1());
                model.addAttribute("savingBalance", balances.getT2());
            } catch ( Exception ex) {
                log.error("Error finding balances", ex);
            }
        }
        model.addAttribute("transferAmount", new TransferAmount());
        return "index";
    }

    @PostMapping("transferToSaving")
    public String transferToSaving(@ModelAttribute TransferAmount transferAmount, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            TransferRequestDto transferRequestDto = TransferRequestDto.builder()
                    .sourceRequestAccount("checking")
                    .targetRequestAccount("saving")
                    .amount(transferAmount.getAmount().toString())
                    .principalName(authentication.getName())
                    .build();
            webClient.post()
                    .uri("http://localhost:8120/api/v1/createTransferRequest")
                    .bodyValue(transferRequestDto)
                    .attributes(clientRegistrationId("auth0-login"))
                    .retrieve().bodyToMono(Void.class).subscribe();
        }
        return setIndexModel(model);
    }
    @PostMapping("transferToChecking")
    public String transferToChecking(@ModelAttribute TransferAmount transferAmount, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            TransferRequestDto transferRequestDto = TransferRequestDto.builder()
                    .sourceRequestAccount("saving")
                    .targetRequestAccount("checking")
                    .amount(transferAmount.getAmount().toString())
                    .principalName(authentication.getName())
                    .build();
            webClient.post()
                    .uri("http://localhost:8120/api/v1/createTransferRequest")
                    .bodyValue(transferRequestDto)
                    .attributes(clientRegistrationId("auth0-login"))
                    .retrieve().bodyToMono(Void.class)
                    .subscribe();
        }
        return setIndexModel(model);
    }
}
