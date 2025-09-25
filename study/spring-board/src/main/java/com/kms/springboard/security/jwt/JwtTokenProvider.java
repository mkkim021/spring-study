package com.kms.springboard.security.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    private final Key key = getKey();

    /**
     * Create a signed JWT containing the given subject and expiration.
     *
     * @param subject   the value to set as the token's subject (typically a user identifier)
     * @param expiredAt the token expiration time
     * @return          the compact serialized JWT string
     */
    public String generate(String subject, Date expiredAt){
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expiredAt)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }





    /**
     * Extracts the JWT subject claim after validating the token's signature.
     *
     * @param token the JWT string to parse
     * @return the subject from the token's claims (typically the user identifier)
     */
    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /**
     * Validates a JWT string using the provider's signing key.
     *
     * @param token the JWT compact serialized string to validate
     * @return {@code true} if the token is correctly signed and can be parsed, {@code false} otherwise
     */
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (JwtException | IllegalArgumentException e) {
            log.debug("Invalid JWT token", e);
            return false;
        }
    }
    /**
     * Create the HMAC-SHA signing key from the configured Base64-encoded JWT secret.
     *
     * @return the HMAC-SHA `Key` derived from the configured Base64-encoded `jwtSecret`
     */
    @PostConstruct
    public Key getKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }
}
