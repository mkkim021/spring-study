package com.kms.springboard.auth.service;

public interface TokenService {
    void saveRefreshToken(String userId, String refreshToken);
    boolean validateRefreshToken(String userId, String refreshToken);
    void deleteRefreshToken(String userId);
    void addToBlacklist(String accessToken);
    boolean isBlacklisted(String accessToken);

}
