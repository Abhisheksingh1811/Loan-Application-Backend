package com.yasirakbal.secureloanapi.feature.auth.service;

import com.yasirakbal.secureloanapi.common.exception.BusinessException;
import com.yasirakbal.secureloanapi.feature.audit.annotation.Auditable;
import com.yasirakbal.secureloanapi.feature.audit.enums.AuditEventType;
import com.yasirakbal.secureloanapi.feature.audit.service.LoginHistoryService;
import com.yasirakbal.secureloanapi.feature.audit.utils.RequestInfoUtils;
import com.yasirakbal.secureloanapi.feature.auth.adapter.AppUserAdapter;
import com.yasirakbal.secureloanapi.feature.auth.dto.LoginResponse;
import com.yasirakbal.secureloanapi.feature.auth.dto.RefreshTokenResponse;
import com.yasirakbal.secureloanapi.feature.auth.entity.RefreshToken;
import com.yasirakbal.secureloanapi.feature.auth.exception.*;
import com.yasirakbal.secureloanapi.feature.auth.repository.RefreshTokenRepository;
import com.yasirakbal.secureloanapi.feature.blacklist.entity.JwtBlacklist;
import com.yasirakbal.secureloanapi.feature.blacklist.enums.JwtBlacklistReason;
import com.yasirakbal.secureloanapi.feature.blacklist.service.JwtBlacklistService;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.enums.UserRole;
import com.yasirakbal.secureloanapi.feature.user.exception.*;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import com.yasirakbal.secureloanapi.feature.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JwtEncoder jwtEncoder;
    private JwtBlacklistService jwtBlacklistService;
    private LoginHistoryService loginHistoryService;
    private UserService userService;
    private RefreshTokenService refreshTokenService;
    private RefreshTokenRepository refreshTokenRepository;
    private LoginRateLimiterService loginRateLimiterService;

    @Transactional
    @Auditable(eventType = AuditEventType.USER_CREATED, resource = "#userToCreate.username")
    public User registerUser(User userToCreate) {
        List<BusinessException> validationErrors = validateUser(userToCreate);
        if(!validationErrors.isEmpty()) {
            throw new UserCreationValidationException(validationErrors, "User registration failed.", HttpStatus.CONFLICT);
        }

        String hashedPassword = passwordEncoder.encode(userToCreate.getPassword());
        userToCreate.setPassword(hashedPassword);
        userToCreate.setRole(UserRole.CUSTOMER);

        return userRepository.save(userToCreate);
    }

    private List<BusinessException> validateUser(User userToCreate) {
        List<BusinessException> validationErrors = new ArrayList<>();

        if(userRepository.existsUserByEmail(userToCreate.getEmail())) {
            validationErrors.add(new EmailDuplicationException(userToCreate.getEmail()));
        }
        if(userRepository.existsUserByUsername(userToCreate.getUsername())) {
            validationErrors.add(new UsernameDuplicationException(userToCreate.getUsername()));
        }
        if(userRepository.existsUserByIdentityNumber(userToCreate.getIdentityNumber())) {
            validationErrors.add(new IdentityNumberDuplicationException(userToCreate.getIdentityNumber()));
        }

        return validationErrors;
    }

    @Transactional
    public LoginResponse login(String username, String password, HttpServletRequest httpRequest) {
        RequestInfoUtils.RequestInfo requestInfo = RequestInfoUtils.extract(httpRequest);
        loginRateLimiterService.checkLoginRateLimit(requestInfo.getIpAddress());

        try {
            var authToken = new UsernamePasswordAuthenticationToken(username, password);
            var authentication = authenticationManager.authenticate(authToken);

            AppUserAdapter userAdapter = (AppUserAdapter) authentication.getPrincipal();

            loginHistoryService.saveSuccessfulLogin(userAdapter.getUser(), requestInfo);
            log.info(
                    "User login successful | username={} | userId={}",
                    userAdapter.getUsername(),
                    userAdapter.getUser().getId()
            );


            return getLoginResponse(userAdapter);

        } catch (LockedException e) {
            User user = userRepository.findUserByUsername(username).orElse(null);
            loginHistoryService.saveFailedLogin(user, requestInfo, "Account locked");
            throw new UserAccountLockedException(user.getLockedUntil());

        } catch (DisabledException e) {
            User user = userRepository.findUserByUsername(username).orElse(null);
            loginHistoryService.saveFailedLogin(user, requestInfo, "Account disabled");
            throw new UserAccountDisabledException();

        } catch (CredentialsExpiredException e) {
            User user = userRepository.findUserByUsername(username).orElse(null);
            loginHistoryService.saveFailedLogin(user, requestInfo, "Password expired");
            userService.markPasswordAsExpired(user.getId());
            throw new UserPasswordExpiredException();

        } catch (BadCredentialsException e) {
            User user = userRepository.findUserByUsername(username).orElse(null);
            if (user != null) {
                loginHistoryService.saveFailedLogin(user, requestInfo, "Invalid password");
                log.warn(
                        "User login failed | username={} | userId={} | reason=Invalid password",
                        username,
                        user.getId()
                );

                throw userService.handleFailedLogin(user.getId());
            } else {
                loginHistoryService.saveAnonymousAttempt(username, requestInfo);
                throw new InvalidCredentialsException();
            }
        }
    }

    private LoginResponse getLoginResponse(AppUserAdapter userAdapter) {
        User user = userAdapter.getUser();
        String token = generateToken(userAdapter);
        String refreshToken = refreshTokenService.generateRefreshToken(user.getId());

        LoginResponse.LoginUserResponse userResponse = LoginResponse.LoginUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().getValue())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();

        return LoginResponse.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(30 * 60)
                .user(userResponse)
                .build();
    }

    private String generateToken(AppUserAdapter userAdapter) {
        User user = userAdapter.getUser();
        var authorities = userAdapter.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));

        var claimsSet = JwtClaimsSet.builder()
                .subject(userAdapter.getUsername())
                .issuer("secureloan-api")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plus(30, ChronoUnit.MINUTES))
                .claim("scope", authorities)
                .claim("userId", user.getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    public void logoutUser(Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        String token = jwt.getTokenValue();
        LocalDateTime expiresAt = LocalDateTime.ofInstant(jwt.getExpiresAt(), ZoneOffset.UTC);

        JwtBlacklist jwtBlacklist = JwtBlacklist.builder()
                .userId(userId)
                .reason(JwtBlacklistReason.LOGOUT)
                .blacklistedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .token(token)
                .build();

        jwtBlacklistService.createJwtBlacklist(jwtBlacklist);
    }

    @Transactional
    public RefreshTokenResponse refreshToken(String refreshToken) {
        RefreshToken oldToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(InvalidRefreshTokenException::new);

        Long userId = refreshTokenService.validateRefreshTokenAndGetUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        String accessToken = generateToken(new AppUserAdapter(user));
        String newRefreshToken = refreshTokenService.generateRefreshTokenWithAbsoluteExpiry(
                user.getId(),
                oldToken.getAbsoluteExpiryTime()
        );

        return RefreshTokenResponse.builder()
                .refreshToken(newRefreshToken)
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(30 * 60)
                .build();
    }
}
