package com.example.android_project.service;

import com.example.android_project.repository.UserRepository;
import com.example.android_project.dto.GoogleResponseDTO;
import com.example.android_project.dto.KakaoResponseDTO;
import com.example.android_project.dto.NaverResponseDTO;
import com.example.android_project.dto.OAuth2ResponseDTO;
import com.example.android_project.dto.UserDTO;
import com.example.android_project.entity.UserEntity;
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
        //System.out.println(oAuth2User);

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
        String username = oAuth2ResponseDTO.getProvider()+" "+oAuth2ResponseDTO.getProviderId();
        UserEntity existData = userRepository.findByUsername(username);

        if (existData == null) {

            UserEntity userEntity = new UserEntity();
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2ResponseDTO.getEmail());
            userEntity.setName(oAuth2ResponseDTO.getName());

            userRepository.save(userEntity);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2ResponseDTO.getName());

            return new CustomOAuth2User(userDTO);
        }
        else {

            existData.setEmail(oAuth2ResponseDTO.getEmail());
            existData.setName(oAuth2ResponseDTO.getName());

            userRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getUsername());
            userDTO.setName(oAuth2ResponseDTO.getName());

            return new CustomOAuth2User(userDTO);
        }
    }
}
