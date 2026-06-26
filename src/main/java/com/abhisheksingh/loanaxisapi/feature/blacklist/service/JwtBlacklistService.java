package com.abhisheksingh.loanaxisapi.feature.blacklist.service;

import com.abhisheksingh.loanaxisapi.common.redis.RedisService;
import com.abhisheksingh.loanaxisapi.feature.audit.annotation.Auditable;
import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import com.abhisheksingh.loanaxisapi.feature.blacklist.entity.JwtBlacklist;
import com.abhisheksingh.loanaxisapi.feature.blacklist.repository.JwtBlacklistRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
@Slf4j
public class JwtBlacklistService {

    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";

    private JwtBlacklistRepository jwtBlacklistRepository;
    private RedisService redisService;

    @Transactional
    @Auditable(eventType = AuditEventType.TOKEN_BLACKLISTED)
    public void createJwtBlacklist(JwtBlacklist jwtBlacklist) {
        jwtBlacklistRepository.save(jwtBlacklist);

        Duration ttl = Duration.between(
                LocalDateTime.now(ZoneOffset.UTC),
                jwtBlacklist.getExpiresAt()
        );

        if (!ttl.isNegative() && !ttl.isZero()) {
            redisService.set(
                    JWT_BLACKLIST_PREFIX + jwtBlacklist.getToken(),
                    "blacklisted",
                    ttl
            );

            log.info("JWT token blacklisted in Redis with TTL {} seconds", ttl.toSeconds());
        } else {
            log.warn("JWT token was not cached in Redis because TTL was invalid: {} seconds", ttl.toSeconds());
        }
    }

    public boolean isBlacklisted(String jwtToken) {
        String redisKey = JWT_BLACKLIST_PREFIX + jwtToken;

        if (redisService.exists(redisKey)) {
            log.info("JWT blacklist HIT from Redis");
            return true;
        }

        log.info("JWT blacklist MISS from Redis. Checking database...");

        boolean existsInDb = jwtBlacklistRepository.existsJwtBlacklistByToken(jwtToken);

        if (existsInDb) {
            log.info("JWT blacklist HIT from Database. Re-caching in Redis");

            redisService.set(
                    redisKey,
                    "blacklisted",
                    Duration.ofMinutes(30)
            );
        }

        return existsInDb;
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = jwtBlacklistRepository.deleteByExpiresAtBefore(
                LocalDateTime.now(ZoneOffset.UTC)
        );

        if (deleted > 0) {
            log.info("Cleaned up {} expired tokens from blacklist", deleted);
        }
    }
}
