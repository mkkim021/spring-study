package com.kms.springboard.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisTokenService implements TokenService {

    private final RedisTemplate<String, String>stringRedisTemplate;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        Duration expiration = Duration.ofMillis(refreshExpirationMs);
        stringRedisTemplate.opsForValue().set(key, refreshToken, expiration);
        log.debug("Redis: Refresh token 저장 - 사용자: {}", userId);
    }

    @Override
    public boolean validateRefreshToken(String userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        String storedToken = stringRedisTemplate.opsForValue().get(key);
        boolean isValid = refreshToken.equals(storedToken);
        log.debug("Redis: Refresh token 검증 - 사용자 {}: {}", userId, isValid);
        return isValid;
    }

    @Override
    public void deleteRefreshToken(String userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        stringRedisTemplate.delete(key);
        log.debug("Redis: Refresh token 삭제 - 사용자: {}", userId);
    }

    @Override
    public void addToBlacklist(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        Duration expiration = Duration.ofMillis(accessExpirationMs);
        stringRedisTemplate.opsForValue().set(key, "blacklist", expiration);
        log.debug("Redis: 블랙리스트 추가");
    }

    @Override
    public boolean isBlacklisted(String accessToken) {
        String key = BLACKLIST_PREFIX + accessToken;
        Boolean exists = stringRedisTemplate.hasKey(key);
        boolean isBlacklisted = Boolean.TRUE.equals(exists);
        return isBlacklisted;
    }
}
