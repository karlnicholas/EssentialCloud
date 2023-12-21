package com.example.essentialcloud.savingaccount;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
public class SavingAccountController {
    private final SavingAccountService savingAccountService;
    private final ObjectMapper objectMapper;

    public SavingAccountController(SavingAccountService savingAccountService, ObjectMapper objectMapper) {
        this.savingAccountService = savingAccountService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("account")
    public String getBalance(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return savingAccountService.balance(getSavingAccountId(authentication.getName())).toString();
        }
        return "";
    }
        @GetMapping("withdraw")
    public Long withdraw(@RequestParam("principalName") String principalName, @RequestParam("amount") BigDecimal amount){
        return savingAccountService.withdraw(getSavingAccountId(principalName), amount);
    }

    @GetMapping("deposit")
    public Long deposit(@RequestParam("principalName") String principalName,  @RequestParam("amount") BigDecimal amount){
        return savingAccountService.deposit(getSavingAccountId(principalName), amount);
    }

    @GetMapping("verifyTransfer")
    public Long verifyTransfer(@RequestParam("transferId") Long transferId){
        return savingAccountService.verifytransfer(transferId);
    }

    @Autowired
    WebClient webClient;
    private Long getSavingAccountId(String principalName) {
        return webClient.get()
                .uri(builder -> builder
                        .path("/api/v1/userinfo")
                        .queryParam("authenticationId", principalName)
                        .build())
                .attributes(clientRegistrationId("auth0-login"))
                .retrieve()
                .bodyToMono(String.class)
                .<Long>handle((userInfo, sink) -> {
                    try {
                        sink.next(objectMapper.readTree(userInfo).findValue("savingAccountId").asLong());
                    } catch (JsonProcessingException e) {
                        sink.error(new RuntimeException(e));
                    }
                })
                .block();
    }
}
