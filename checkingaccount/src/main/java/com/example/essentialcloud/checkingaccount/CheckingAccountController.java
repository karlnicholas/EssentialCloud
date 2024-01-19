package com.example.essentialcloud.checkingaccount;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;

@RestController
@RequestMapping("/api/v1")
public class CheckingAccountController {
    private final CheckingAccountService checkingAccountService;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CheckingAccountController(CheckingAccountService checkingAccountService, WebClient webClient, ObjectMapper objectMapper) {
        this.checkingAccountService = checkingAccountService;
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    @GetMapping("account")
    public String getBalance(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return checkingAccountService.balance(getCheckingAccountId(authentication.getName())).toString();
        }
        return "";
    }
    @GetMapping("withdraw")
    public Long withdraw(@RequestParam("principalName") String principalName, @RequestParam("amount") BigDecimal amount){
        return checkingAccountService.withdraw(getCheckingAccountId(principalName), amount);
    }

    @GetMapping("deposit")
    public Long deposit(@RequestParam("principalName") String principalName,  @RequestParam("amount") BigDecimal amount){
        return checkingAccountService.deposit(getCheckingAccountId(principalName), amount);
    }

    @GetMapping("verifyTransfer")
    public Long verifyTransfer(@RequestParam("transferId") Long transferId){
        return checkingAccountService.verifytransfer(transferId);
    }

    private Long getCheckingAccountId(String principalName) {
        return webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/userinfo")
                        .queryParam("authenticationId", principalName)
                        .build())
                .attributes(clientRegistrationId("auth0-api"))
                .retrieve()
                .bodyToMono(String.class)
                .<Long>handle((userInfo, sink) -> {
                    try {
                        sink.next(objectMapper.readTree(userInfo).findValue("checkingAccountId").asLong());
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .block();
    }
}
