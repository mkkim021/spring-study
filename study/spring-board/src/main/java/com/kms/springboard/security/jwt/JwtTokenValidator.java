package com.kms.springboard.security.jwt;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenValidator {

    private final JwtTokenProvider jwtTokenProvider;

    public boolean validate(String token) {
        return jwtTokenProvider.validateToken(token);
    }

}
