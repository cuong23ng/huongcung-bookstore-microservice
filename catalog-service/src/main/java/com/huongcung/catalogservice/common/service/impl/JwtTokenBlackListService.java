package com.huongcung.catalogservice.common.service.impl;

import com.huongcung.catalogservice.common.provider.TokenProvider;
import com.huongcung.catalogservice.common.service.TokenBlackListService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenBlackListService implements TokenBlackListService {

    private final TokenProvider tokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean isTokenBlacklisted(String token) {
        Boolean isBlacklisted = redisTemplate.hasKey("BL_" + token);
        return Boolean.TRUE.equals(isBlacklisted);
    }

    public void removeFromBlacklist(String token) {

    }

    public int getBlacklistSize() {
        return 0;
    }

    public void clearBlacklist() {

    }
}
