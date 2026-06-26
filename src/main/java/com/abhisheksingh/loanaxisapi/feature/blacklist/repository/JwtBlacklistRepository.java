package com.abhisheksingh.loanaxisapi.feature.blacklist.repository;

import com.abhisheksingh.loanaxisapi.feature.blacklist.entity.JwtBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDateTime;

public interface JwtBlacklistRepository extends JpaRepository<JwtBlacklist, Long> {
    boolean existsJwtBlacklistByToken(String token);

    @Modifying
    int deleteByExpiresAtBefore(LocalDateTime date);
}
