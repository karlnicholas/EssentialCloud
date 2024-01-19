package com.example.essentialcloud.bff;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.function.Consumer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    @Value("${spring.security.oauth2.client.provider.auth0.issuer-uri}")
    private String issuer;
    @Value("${spring.security.oauth2.client.registration.auth0-login.client-id}")
    private String clientId;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers( "/", "/dist/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2Login(withDefaults())
                .logout(logout->logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout")).addLogoutHandler(oidcLogoutHandler()));


            http.oauth2Login(oauth2 -> oauth2
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestResolver(
                                authorizationRequestResolver(this.clientRegistrationRepository)
                        )
                )
        );

        return http.build();
    }
    private LogoutHandler oidcLogoutHandler() {
        return (request, response, authentication) -> {
            try {
                response.sendRedirect( issuer + "v2/logout?client_id=" + clientId + "&returnTo=http://localhost:8080/");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }



    private OAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ClientRegistrationRepository clientRegistrationRepository) {

        DefaultOAuth2AuthorizationRequestResolver authorizationRequestResolver =
                new DefaultOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository, "/oauth2/authorization");
        authorizationRequestResolver.setAuthorizationRequestCustomizer(
                authorizationRequestCustomizer());

        return authorizationRequestResolver;
    }

    private Consumer<OAuth2AuthorizationRequest.Builder> authorizationRequestCustomizer() {
        return customizer -> customizer
                .additionalParameters(params -> params.put("audience", "https://essentialcloud"));
    }

}
