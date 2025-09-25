package com.kms.springboard.security.jwt;

import com.kms.springboard.security.jwt.dto.JwtTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenGenerator {

    private final JwtTokenProvider jwtTokenProvider;
    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    private final Key key = getKey();

    public JwtTokenGenerator(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public String generateRefreshToken(String userId){
        Date refreshTokenExpiration = new Date(System.currentTimeMillis() + jwtExpirationMs * 70);
        Date accessTokenExpiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

        String accessToken = jwtTokenProvider.generate(userId, accessTokenExpiration);
        String refreshToken = jwtTokenProvider.generate(userId, refreshTokenExpiration);

        return JwtTokenResponse.of(accessToken, refreshToken)
    }

    public String generateAccessToken(String userId){

        return Jwts.builder()
                .setSubject(userId)
                .claim("type","access_token")
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    @PostConstruct
    public Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
