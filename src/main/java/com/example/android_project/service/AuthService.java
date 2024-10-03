package com.example.android_project.service;

import com.example.android_project.dto.SignupRequestDTO;

public interface AuthService {

    String registerUser(SignupRequestDTO signUpRequestDTO);
}
