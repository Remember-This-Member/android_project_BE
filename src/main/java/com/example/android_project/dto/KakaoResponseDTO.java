package com.example.android_project.dto;

import java.util.Map;

public class KakaoResponseDTO implements OAuth2ResponseDTO{

    private final Map<String, Object> attribute;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> properties;

    public KakaoResponseDTO(Map<String, Object> attribute) {
        this.attribute = attribute;
        this.kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        this.properties = (Map<String, Object>) attribute.get("properties");
    }

    @Override
    public String getProvider() {

        return "kakao";
    }

    @Override
    public String getProviderId() {

        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {

        return kakaoAccount.get("email").toString();
    }

    @Override
    public String getName() {

        return properties.get("nickname").toString();
    }
}
