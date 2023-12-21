package com.example.essentialcloud.bff;

import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

public class CustomRefreshRequestEntityConverter implements
        Converter<OAuth2RefreshTokenGrantRequest, RequestEntity<?>> {

    private OAuth2RefreshTokenGrantRequestEntityConverter defaultConverter;

    public CustomRefreshRequestEntityConverter() {
        defaultConverter = new OAuth2RefreshTokenGrantRequestEntityConverter();
    }

    @Override
    public RequestEntity<?> convert(OAuth2RefreshTokenGrantRequest req) {
        RequestEntity<?> entity = defaultConverter.convert(req);
        MultiValueMap<String, String> params = (MultiValueMap<String,String>) entity.getBody();
        params.add("audience", "https://jwtresourceapi");
        return new RequestEntity<>(params, entity.getHeaders(),
                entity.getMethod(), entity.getUrl());
    }

}