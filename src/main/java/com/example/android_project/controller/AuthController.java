package com.example.android_project.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    // 쿠키에 담긴 토큰을 반환하는 로직
    @GetMapping("/token")
    public ResponseEntity<Void> getToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        // 쿠키에서 JWT 토큰 추출
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                jwtToken = cookie.getValue();
            }
        }

        // JWT 토큰이 없으면 401 응답
        if (jwtToken == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // JWT를 Authorization 헤더에 담아 반환
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", jwtToken);

        return ResponseEntity.ok().headers(headers).build();
    }
}
