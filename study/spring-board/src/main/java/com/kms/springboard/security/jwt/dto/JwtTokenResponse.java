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

    /**
     * Create a JwtTokenResponse populated with the given tokens and metadata.
     *
     * @param accessToken the issued access token
     * @param refreshToken the issued refresh token
     * @param expiredIn the expiration duration for the access token
     * @param grantType the grant type associated with the tokens
     * @return a JwtTokenResponse containing the provided token values and metadata
     */
    public static JwtTokenResponse of(String accessToken, String refreshToken, Long expiredIn, String grantType) {
        return new JwtTokenResponse(accessToken, refreshToken, expiredIn, grantType);
    }

}
