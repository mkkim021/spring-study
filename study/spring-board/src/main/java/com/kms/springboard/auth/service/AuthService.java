package com.kms.springboard.auth.service;

import com.kms.springboard.auth.dto.JwtTokenResponse;
import com.kms.springboard.auth.dto.LoginDto;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    JwtTokenResponse login(LoginDto loginDto);
    JwtTokenResponse refreshToken(String refreshToken);

    void logout(String authHeader, String refreshToken, HttpServletResponse response);

    void setRefreshTokenCookie(HttpServletResponse response, String refreshToken);

    boolean validateToken(String authHeader);
}
