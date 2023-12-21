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
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@Controller
@Slf4j
public class BfftbController {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BfftbController(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/transferbroker")
    public String home(Model model) throws JsonProcessingException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String transfers = webClient.get()
                    .uri(uriBuilder -> uriBuilder.scheme("http").host("localhost").port(8120).path("/api/v1/allTransferRequests").build())
                    .attributes(clientRegistrationId("auth0-login"))
                    .retrieve().bodyToMono(String.class).block();
            List<TransferModel> transferList = objectMapper.readValue(transfers, new TypeReference<>() {});
            model.addAttribute("transfers", transferList);
        }
        return setIndexModel(model);
    }

    private String setIndexModel(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            model.addAttribute("profile", authentication.getPrincipal());
        }
        return "transferbroker";
    }

}
