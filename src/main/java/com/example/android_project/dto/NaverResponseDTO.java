package com.example.android_project.dto;

import java.util.Map;

public class NaverResponseDTO implements OAuth2ResponseDTO{

    private final Map<String, Object> attribute;

    public NaverResponseDTO(Map<String, Object> attribute) {

        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {

        return "naver";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return attribute.get("email").toString();
    }

    @Override
    public String getName() {

        return attribute.get("name").toString();
    }
}
