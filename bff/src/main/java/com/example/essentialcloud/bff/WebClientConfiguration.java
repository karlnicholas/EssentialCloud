package com.example.essentialcloud.bff;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfiguration {
    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        return WebClient.builder()
                .apply(oauth2Client.oauth2Configuration())
                .build();
    }
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .refreshToken()
//                        .clientCredentials((builder) ->
//                                builder.accessTokenResponseClient(clientCredentialsAccessTokenResponseClient())
//                                        .build())
//                        .refreshToken(refreshTokenGrantBuilder ->
//                                refreshTokenGrantBuilder.accessTokenResponseClient(clientCredentialsRefreshTokenResponseClient())
//                                        .build())
                        .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
//
//    public OAuth2AccessTokenResponseClient<OAuth2ClientCredentialsGrantRequest> clientCredentialsAccessTokenResponseClient() {
//
//        DefaultClientCredentialsTokenResponseClient accessTokenResponseClient =
//                new DefaultClientCredentialsTokenResponseClient();
//        accessTokenResponseClient.setRequestEntityConverter(new CustomClientCredRequestEntityConverter());
//
//        return accessTokenResponseClient;
//    }
//    public OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> clientCredentialsRefreshTokenResponseClient() {
//
//        DefaultRefreshTokenTokenResponseClient accessTokenResponseClient =
//                new DefaultRefreshTokenTokenResponseClient();
//        accessTokenResponseClient.setRequestEntityConverter(new CustomRefreshRequestEntityConverter());
//
//        return accessTokenResponseClient;
//    }
//
//    private static Converter<OAuth2ClientCredentialsGrantRequest, MultiValueMap<String, String>> parametersConverter() {    };

}
