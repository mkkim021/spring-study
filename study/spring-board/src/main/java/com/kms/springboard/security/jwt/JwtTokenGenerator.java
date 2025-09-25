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

    /**
     * Creates a JwtTokenGenerator using the provided JwtTokenProvider.
     */
    public JwtTokenGenerator(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    /**
     * Generate a paired access token and refresh token for the specified user.
     *
     * @param userId the identifier to set as the subject of both tokens
     * @return a JwtTokenResponse containing the generated access token and refresh token
     */
    public String generateRefreshToken(String userId){
        Date refreshTokenExpiration = new Date(System.currentTimeMillis() + jwtExpirationMs * 70);
        Date accessTokenExpiration = new Date(System.currentTimeMillis() + jwtExpirationMs);

        String accessToken = jwtTokenProvider.generate(userId, accessTokenExpiration);
        String refreshToken = jwtTokenProvider.generate(userId, refreshTokenExpiration);

        return JwtTokenResponse.of(accessToken, refreshToken)
    }

    /**
     * Builds a signed JWT access token for the given user.
     *
     * @param userId the user identifier to set as the token's subject
     * @return the signed JWT access token as a compact string
     */
    public String generateAccessToken(String userId){

        return Jwts.builder()
                .setSubject(userId)
                .claim("type","access_token")
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpiration)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }
    /**
     * Create an HMAC-SHA signing key derived from the configured Base64-encoded JWT secret.
     *
     * @return the signing {@code Key} produced by decoding the configured Base64 JWT secret
     */
    @PostConstruct
    public Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

}
