package com.example.android_project.service;

import com.example.android_project.dto.SignInRequestDTO;
import com.example.android_project.dto.SignupRequestDTO;
import com.example.android_project.entity.UserProfile;

public interface AuthService {

    Long registerUser(SignupRequestDTO signUpRequestDTO);
    UserProfile signinUser(SignInRequestDTO signInRequestDTO);
}
