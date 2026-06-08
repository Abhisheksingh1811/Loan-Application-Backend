package com.yasirakbal.secureloanapi.feature.auth.service;

import com.yasirakbal.secureloanapi.common.redis.RedisService;
import com.yasirakbal.secureloanapi.feature.auth.exception.LoginRateLimitExceededException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@AllArgsConstructor
public class LoginRateLimiterService {

    private static final int MAX_ATTEMPTS = 10;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private static final String LOGIN_RATE_LIMIT_PREFIX = "rate-limit:login:";

    private RedisService redisService;

    public void checkLoginRateLimit(String ipAddress) {
        String key = LOGIN_RATE_LIMIT_PREFIX + ipAddress;

        long attempts = redisService.increment(key);

        if (attempts == 1) {
            redisService.expire(key, WINDOW);
        }

        if (attempts > MAX_ATTEMPTS) {
            long ttl = redisService.getTtlSeconds(key);

            throw new LoginRateLimitExceededException(ttl);
        }
    }
}