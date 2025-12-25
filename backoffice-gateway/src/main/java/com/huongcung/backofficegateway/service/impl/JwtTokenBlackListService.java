package com.huongcung.backofficegateway.service.impl;

import com.huongcung.backofficegateway.provider.TokenProvider;
import com.huongcung.backofficegateway.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenBlackListService implements TokenBlackListService {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public void blacklistToken(String token) {
        Date expiration = tokenProvider.extractExpiration(token);
        long ttl = expiration.getTime() - System.currentTimeMillis();

        if (ttl > 0) {
            redisTemplate.opsForValue()
                    .set("BL_" + token, "LOGOUT", ttl, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        Boolean isBlacklisted = redisTemplate.hasKey("BL_" + token);
        return Boolean.TRUE.equals(isBlacklisted);
    }
}
