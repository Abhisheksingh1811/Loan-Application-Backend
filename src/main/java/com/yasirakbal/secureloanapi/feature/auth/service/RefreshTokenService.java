package com.yasirakbal.secureloanapi.feature.auth.service;

import com.yasirakbal.secureloanapi.feature.auth.entity.RefreshToken;
import com.yasirakbal.secureloanapi.feature.auth.exception.ExpiredRefreshTokenException;
import com.yasirakbal.secureloanapi.feature.auth.exception.InvalidRefreshTokenException;
import com.yasirakbal.secureloanapi.feature.auth.exception.RefreshTokenAlreadyUsedException;
import com.yasirakbal.secureloanapi.feature.auth.exception.SessionExpiredException;
import com.yasirakbal.secureloanapi.feature.auth.repository.RefreshTokenRepository;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final Duration REFRESH_TOKEN_VALIDITY = Duration.ofDays(7);
    private static final Duration ABSOLUTE_SESSION_TIMEOUT = Duration.ofDays(30);

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Lazy
    @Autowired
    private RefreshTokenService self;

    @Transactional
    public String generateRefreshToken(Long userId) {
        final User user = userRepository.findById(userId).orElseThrow();
        final String newToken = UUID.randomUUID().toString();

        LocalDateTime now = LocalDateTime.now();


        final RefreshToken refreshToken = RefreshToken.builder()
                .token(newToken)
                .expirationTime(now.plus(REFRESH_TOKEN_VALIDITY))
                .absoluteExpiryTime(now.plus(ABSOLUTE_SESSION_TIMEOUT))
                .isTokenUsedBefore(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);

        return newToken;
    }

    @Transactional
    public Long validateRefreshTokenAndGetUserId(final String givenToken) {
        final RefreshToken refreshToken = refreshTokenRepository
                .findByToken(givenToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        LocalDateTime now = LocalDateTime.now();
        Long userId = refreshToken.getUser().getId();

        if(refreshToken.getExpirationTime().isBefore(now)) {
            throw new ExpiredRefreshTokenException();
        }

        if (refreshToken.getAbsoluteExpiryTime().isBefore(now)) {
            self.deleteUsersAllRefreshTokens(userId);
            throw new SessionExpiredException();
        }

        if(refreshToken.getIsTokenUsedBefore()) {
            self.deleteUsersAllRefreshTokens(userId);
            throw new RefreshTokenAlreadyUsedException();
        }

        refreshToken.setIsTokenUsedBefore(true);
        refreshTokenRepository.save(refreshToken);

        return userId;
    }

    @Transactional
    public String generateRefreshTokenWithAbsoluteExpiry(
            Long userId,
            LocalDateTime originalAbsoluteExpiry) {

        User user = userRepository.findById(userId).orElseThrow();
        String newToken = UUID.randomUUID().toString();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(newToken)
                .expirationTime(LocalDateTime.now().plus(REFRESH_TOKEN_VALIDITY))
                .absoluteExpiryTime(originalAbsoluteExpiry)
                .isTokenUsedBefore(false)
                .user(user)
                .build();

        refreshTokenRepository.save(refreshToken);
        return newToken;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUsersAllRefreshTokens(Long userId) {
        refreshTokenRepository.deleteAllByUserId(userId);
    }

    @Scheduled(cron = "0 0 3 * * *") // her gece 03:00
    @Transactional
    public void cleanupExpiredTokens() { //sln
        refreshTokenRepository.deleteAllByExpirationTimeBefore(LocalDateTime.now());
    }
}