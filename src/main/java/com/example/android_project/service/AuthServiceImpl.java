package com.example.android_project.service;

import com.example.android_project.dto.SignupRequestDTO;
import com.example.android_project.entity.User;
import com.example.android_project.entity.UserProfile;
import com.example.android_project.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(SignupRequestDTO signupRequestDTO) {
        // 이미 존재하는 사용자 확인
        if (userRepository.findByUsername(signupRequestDTO.getId()) != null) {
            throw new RuntimeException("이미 존재하는 ID입니다.");
        }

        // 사용자 정보 생성
        UserProfile userProfile = UserProfile.builder()
            .nickname(signupRequestDTO.getNickname())
            .build();

        // 새 사용자 생성
        User user = User.builder()
            .username(signupRequestDTO.getId())
            .name(signupRequestDTO.getNickname())
            .email(signupRequestDTO.getEmail())
            .password(passwordEncoder.encode(signupRequestDTO.getPassword()))
            .userProfile(userProfile)
            .build();

        // 사용자 저장
        userRepository.save(user);

        return user.getUserProfile().getNickname();
    }


}
