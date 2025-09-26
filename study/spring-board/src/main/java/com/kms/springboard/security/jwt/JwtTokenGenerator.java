package com.kms.springboard.security.jwt;

import com.kms.springboard.security.jwt.dto.JwtTokenResponse;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-expiration-ms:604800000}")
    private long refreshTokenExpiration;


   public JwtTokenResponse generateToken(String userId){
       Date now = new Date();
       Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpiration);
       Date refreshTokenExpiryDate = new Date(now.getTime() + refreshTokenExpiration);

       String accessToken = jwtTokenProvider.generate(userId, accessTokenExpiryDate);
       String refreshToken = jwtTokenProvider.generate(userId, refreshTokenExpiryDate);

       return JwtTokenResponse.of(
               accessToken,
               refreshToken,
               accessTokenExpiration / 1000,
               "Bearer"
       );
   }

    public String generateAccessToken(String userId) {
       Date now = new Date();
       Date accessTokenExpiryDate = new Date(now.getTime() + accessTokenExpiration);

       return jwtTokenProvider.generate(userId, accessTokenExpiryDate);
    }



}
