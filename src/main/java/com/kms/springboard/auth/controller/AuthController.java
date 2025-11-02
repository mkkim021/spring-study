package com.kms.springboard.auth.controller;


import com.kms.springboard.auth.dto.JwtTokenResponse;
import com.kms.springboard.auth.dto.LoginDto;
import com.kms.springboard.auth.dto.RefreshTokenRequestDto;
import com.kms.springboard.auth.service.AuthService;
import com.kms.springboard.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtTokenResponse>>login(
            @Valid @RequestBody LoginDto loginDto,
            HttpServletResponse response){
        log.info("Login attempt for user: {}", loginDto.getUserId());

        JwtTokenResponse tokenResponse = authService.login(loginDto);
        authService.setRefreshTokenCookie(response, tokenResponse.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("로그인 성공", tokenResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> logout(
            HttpServletRequest request,
            @CookieValue(value = "REFRESH_TOKEN", required = false)String refreshToken,
            HttpServletResponse response){

        String authHeader = request.getHeader("Authorization");
        log.info("Logout attempt for user: {}", authHeader);
        log.info("Refresh token: {}", refreshToken);
        authService.logout(authHeader,refreshToken,response);

        return ResponseEntity.ok(ApiResponse.success("로그아웃되었습니다",null));

    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtTokenResponse>> refresh(
            @CookieValue(value = "REFRESH_TOKEN", required = false)String cookieRefreshToken,
            @RequestBody(required = false)RefreshTokenRequestDto requestBody,
            HttpServletResponse response){

        String refreshToken = requestBody != null && requestBody.getRefreshToken() != null
                ? requestBody.getRefreshToken()
                :cookieRefreshToken;

        log.info("Refresh attempt for user: {}", refreshToken);

        JwtTokenResponse tokenResponse = authService.refreshToken(refreshToken);
        authService.setRefreshTokenCookie(response, tokenResponse.getRefreshToken());

        return ResponseEntity.ok(ApiResponse.success("토큰 쿠키 설정 완료",tokenResponse));
    }



}
