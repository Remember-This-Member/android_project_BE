package com.example.android_project.Util;

import com.example.android_project.dto.CustomUserDetails;
import com.example.android_project.dto.SignInRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    //JWTUtil 주입
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        super.setFilterProcessesUrl("/api/auth/signin"); // 로그인 필터 경로 설정
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        ObjectMapper objectMapper = new ObjectMapper();
        SignInRequestDTO signInRequestDTO;

        try {
            signInRequestDTO = objectMapper.readValue(request.getInputStream(), SignInRequestDTO.class);
            //클라이언트 요청에서 username, password 추출
            String username = signInRequestDTO.getId(); // username
            String password = signInRequestDTO.getPassword(); // password

            //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            //token에 담은 검증을 위한 AuthenticationManager로 전달
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("인증 실패", e);
        }
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //UserDetailsS
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        String username = customUserDetails.getUsername();
        String nickname = customUserDetails.getNickname();

        String token = jwtUtil.createJwt(username, 60*60*10L);

        // 토큰 로그 확인
        //System.out.println("Generated JWT Token: " + token);

        response.addHeader("Authorization", "Bearer " + token);

        // 응답 본문에 닉네임 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // JSON 형태로 nickname 응답
            response.getWriter().write("{\"nickname\": \"" + nickname + "\"}");
        } catch (IOException e) {
            throw new RuntimeException("응답 실패", e);
        }
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {

        //로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
    }
}