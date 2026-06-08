package com.yasirakbal.secureloanapi.feature.user.service;

import com.yasirakbal.secureloanapi.common.exception.NonRollbackBusinessException;
import com.yasirakbal.secureloanapi.feature.audit.annotation.Auditable;
import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import com.yasirakbal.secureloanapi.feature.auth.exception.InvalidCredentialsException;
import com.yasirakbal.secureloanapi.feature.auth.exception.UserAccountLockedException;
import com.yasirakbal.secureloanapi.feature.blacklist.entity.JwtBlacklist;
import com.yasirakbal.secureloanapi.feature.blacklist.enums.JwtBlacklistReason;
import com.yasirakbal.secureloanapi.feature.blacklist.service.JwtBlacklistService;
import com.yasirakbal.secureloanapi.feature.user.dto.ChangePasswordRequest;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.exception.UserNotFoundException;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtBlacklistService jwtBlacklistService;

    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return user;
    }

    @Transactional(readOnly = true)
    public User getUserById(long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        return user;
    }

    @Transactional
    @Auditable(eventType = AuditEventType.PASSWORD_CHANGED, resource = "#jwt.getClaim('userId')")
    public void changePassword(ChangePasswordRequest request, Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("The given password is incorrect.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());
        user.setPassword(hashedPassword);
        user.setPasswordChangedAt(LocalDateTime.now());
        user.setPasswordExpired(false);

        String token = jwt.getTokenValue();
        LocalDateTime expiresAt = LocalDateTime.ofInstant(jwt.getExpiresAt(), ZoneOffset.UTC);

        JwtBlacklist jwtBlacklist = JwtBlacklist.builder()
                .userId(userId)
                .reason(JwtBlacklistReason.PASSWORD_CHANGED)
                .blacklistedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .token(token)
                .build();

        jwtBlacklistService.createJwtBlacklist(jwtBlacklist);

        userRepository.save(user);
    }

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            noRollbackFor = NonRollbackBusinessException.class
    )
    @Auditable(eventType = AuditEventType.PASSWORD_CHANGED)
    public RuntimeException handleFailedLogin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        int attempts = user.getFailedLoginAttempts() + 1;
        user.setFailedLoginAttempts(attempts);

        if (attempts >= 5) {
            user.setAccountLocked(true);
            user.setLockedUntil(LocalDateTime.now().plusMinutes(15));
            userRepository.save(user);

            return new UserAccountLockedException(user.getLockedUntil())
                    .addDetail("reason", "Too many failed login attempts")
                    .addDetail("maxAttempts", 5);
        }

        userRepository.save(user);
        return new InvalidCredentialsException()
                .addDetail("remainingAttempts", 5 - attempts);
    }

    //bu metotu admin service'e tasimaliyim
    @Transactional
    @Auditable(eventType = AuditEventType.USER_UNLOCKED, resource = "#jwt.getClaim('userId')")
    public void unlockAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Auditable(eventType = AuditEventType.USER_UNLOCKED)
    public void markPasswordAsExpired(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setPasswordExpired(true);
        userRepository.save(user);
    }
}
