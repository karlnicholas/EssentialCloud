package com.example.essentialcloud.bff;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2ClientCredentialsGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

public class CustomClientCredRequestEntityConverter implements
        Converter<OAuth2ClientCredentialsGrantRequest, RequestEntity<?>> {

    private final OAuth2ClientCredentialsGrantRequestEntityConverter defaultConverter;

    public CustomClientCredRequestEntityConverter() {
        defaultConverter = new OAuth2ClientCredentialsGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2ClientCredentialsGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);
        MultiValueMap<String, String> params = (MultiValueMap<String,String>) entity.getBody();
        params.add("audience", "https://jwtresourceapi");
        return new RequestEntity<>(params, entity.getHeaders(),
                entity.getMethod(), entity.getUrl());
    }

}