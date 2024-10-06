package com.example.android_project.service;

import com.example.android_project.dto.SignInRequestDTO;
import com.example.android_project.dto.SignupRequestDTO;

public interface AuthService {

    String registerUser(SignupRequestDTO signUpRequestDTO);
    String signinUser(SignInRequestDTO signInRequestDTO);
}
