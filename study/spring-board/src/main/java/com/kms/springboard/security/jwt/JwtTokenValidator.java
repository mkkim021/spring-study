package com.kms.springboard.security.jwt;

import com.kms.springboard.common.dto.ApiResponse;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtTokenGenerator jwtTokenGenerator;

    public boolean validate(String token) {
        try{
            Jwts.parserBuilder()
                    .setSigningKey(jwtTokenGenerator.getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    pu
}
