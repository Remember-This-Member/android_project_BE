package com.example.android_project.service;

import com.example.android_project.dto.CustomOAuth2User;
import com.example.android_project.entity.UserProfile;
import com.example.android_project.repository.UserRepository;
import com.example.android_project.dto.GoogleResponseDTO;
import com.example.android_project.dto.KakaoResponseDTO;
import com.example.android_project.dto.NaverResponseDTO;
import com.example.android_project.dto.OAuth2ResponseDTO;
import com.example.android_project.dto.UserDTO;
import com.example.android_project.entity.User;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseDTO oAuth2ResponseDTO = null;
        if (registrationId.equals("naver")) {

            oAuth2ResponseDTO = new NaverResponseDTO(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2ResponseDTO = new GoogleResponseDTO(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2ResponseDTO = new KakaoResponseDTO(oAuth2User.getAttributes());
        }
        else {

            return null;
        }
        String provider = oAuth2ResponseDTO.getProvider();
        String providerId = oAuth2ResponseDTO.getProviderId();

        // 유저가 존재하는지 확인
        User existData = userRepository.findByProviderId(providerId);

        if (existData == null) {

            UserProfile userProfile = UserProfile.builder()
                .nickname(oAuth2ResponseDTO.getName())
                .build();

            User user = User.builder()
                .provider(provider)
                .providerId(providerId)
                .email(oAuth2ResponseDTO.getEmail())
                .userProfile(userProfile)
                .build();

            userRepository.save(user);

            UserDTO userDTO = new UserDTO();
            userDTO.setProviderId(providerId);
            userDTO.setName(oAuth2ResponseDTO.getName());

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.changeemail(oAuth2ResponseDTO.getEmail());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setProviderId(providerId);
            userDTO.setName(oAuth2ResponseDTO.getName());

            return new CustomOAuth2User(userDTO);
        }
    }
}
