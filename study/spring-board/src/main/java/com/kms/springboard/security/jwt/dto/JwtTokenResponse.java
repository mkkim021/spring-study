package com.kms.springboard.security.jwt.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenResponse {
    private String accessToken;
    private String refreshToken;
    private Long expiredIn;
    private String grantType;

    public static JwtTokenResponse of(String accessToken, String refreshToken, Long expiredIn, String grantType) {
        return new JwtTokenResponse(accessToken, refreshToken, expiredIn, grantType);
    }

}
