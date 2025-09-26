package com.kms.springboard.auth.jwt;

import com.kms.springboard.auth.dto.JwtTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpiration;


   public JwtTokenResponse generateToken(String userId) {
       Date now = new Date();
       Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpiration);
       Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpiration);

       String accessToken = jwtTokenProvider.generate(userId, accessTokenExpiryDate);
       String refreshToken = jwtTokenProvider.generate(userId, refreshTokenExpiryDate);

       return JwtTokenResponse.of(
               accessToken,
               refreshToken,
               accessTokenExpiration,
               "Bearer"
       );
   }

}
