package com.example.android_project.Util;

import com.example.android_project.dto.CustomUserDetails;
import com.example.android_project.dto.ResponseMap;
import com.example.android_project.dto.SignInRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
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
            //클라이언트 요청에서 id, password 추출
            String id = signInRequestDTO.getId(); // id
            String password = signInRequestDTO.getPassword(); // password

            //스프링 시큐리티에서 id와 password를 검증하기 위해서는 token에 담아야 함
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(id, password);
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

        String providerId = customUserDetails.getProviderId();

        String token = jwtUtil.createJwt(providerId, 60*60*10L);


        response.addHeader("Authorization", "Bearer " + token);

        // 응답 본문에 userProfile 추가 (Gson을 사용한 방식으로 변경)
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");  // JSON 응답을 위해 Content-Type과 Charset 설정
        response.setCharacterEncoding("UTF-8");  // 문자 인코딩을 UTF-8로 설정
        response.setStatus(HttpServletResponse.SC_OK);  // HTTP 상태 코드 200 설정

        // 응답 본문에 userProfile 추가
        Gson gson = new Gson();
        ResponseMap responseMap = new ResponseMap();  // 메시지를 담을 맵 생성
        responseMap.put("userProfile", customUserDetails.getUserProfile());  // userProfile 추가

        // JSON 응답 전송
        try {
            response.getWriter().write(gson.toJson(responseMap.getMap()));  // Gson을 이용해 Map을 JSON 문자열로 변환하여 응답
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