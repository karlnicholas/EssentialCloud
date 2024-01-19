package com.example.essentialcloud.savingaccount;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.endpoint.DefaultClientCredentialsTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.util.CollectionUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collections;

@Configuration
public class WebClientConfiguration {
    @Bean
    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        return WebClient.builder()
                .baseUrl("http://localhost:8100/")
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials((builder) ->
                                builder.accessTokenResponseClient(clientCredentialsAccessTokenResponseClient())
                                        .build())
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }

    private OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> clientCredentialsAccessTokenResponseClient() {
        DefaultClientCredentialsTokenResponseClient accessTokenResponseClient =
                new DefaultClientCredentialsTokenResponseClient();

        OAuth2ClientCredentialsGrantRequestEntityConverter requestEntityConverter =
                new OAuth2ClientCredentialsGrantRequestEntityConverter();
        requestEntityConverter.addParametersConverter(source -> CollectionUtils.toMultiValueMap(Collections.singletonMap("audience", Collections.singletonList("https://essentialcloud"))));
        accessTokenResponseClient.setRequestEntityConverter(requestEntityConverter);

        return accessTokenResponseClient;
    }
}
