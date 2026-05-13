package com.database.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenService.class);

    @Value("${jwt.refresh.expiration.ms}")
    private long refreshExpirationMs;

    private static final String PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createRefreshToken(String username) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(PREFIX + token, username, refreshExpirationMs, TimeUnit.MILLISECONDS);
        log.info("Refresh token created → username: {}", username);
        return token;
    }

    public String validateAndGetUsername(String token) {
        String username = redisTemplate.opsForValue().get(PREFIX + token);
        if (username == null) {
            log.warn("Refresh token not found or expired");
            throw new RuntimeException("Refresh token expired or not found. Please login again.");
        }
        log.debug("Refresh token valid → username: {}", username);
        return username;
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete(PREFIX + token);
        log.info("Refresh token deleted");
    }
}