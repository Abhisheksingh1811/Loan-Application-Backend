package com.abhisheksingh.loanaxisapi.feature.audit.service;

import com.abhisheksingh.loanaxisapi.feature.audit.entity.LoginHistory;
import com.abhisheksingh.loanaxisapi.feature.audit.repository.LoginHistoryRepository;
import com.abhisheksingh.loanaxisapi.feature.audit.utils.RequestInfoUtils;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import com.abhisheksingh.loanaxisapi.feature.user.exception.UserNotFoundException;
import com.abhisheksingh.loanaxisapi.feature.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LoginHistoryService {
    private LoginHistoryRepository loginHistoryRepository;
    private UserRepository userRepository;

    public List<LoginHistory> getUserLoginsHistory(Long userId) {
        if(!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return loginHistoryRepository.findLoginHistoriesByUserId(userId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSuccessfulLogin(User user, RequestInfoUtils.RequestInfo requestInfo) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .ipAddress(requestInfo.getIpAddress())
                .userAgent(requestInfo.getUserAgent())
                .browser(requestInfo.getBrowser())
                .os(requestInfo.getOs())
                .device(requestInfo.getDevice())
                .success(true)
                .timestamp(LocalDateTime.now())
                .build();

        loginHistoryRepository.save(history);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveFailedLogin(User user, RequestInfoUtils.RequestInfo requestInfo, String failureReason) {
        LoginHistory history = LoginHistory.builder()
                .user(user)
                .ipAddress(requestInfo.getIpAddress())
                .userAgent(requestInfo.getUserAgent())
                .browser(requestInfo.getBrowser())
                .os(requestInfo.getOs())
                .device(requestInfo.getDevice())
                .success(false)
                .failureReason(failureReason)
                .timestamp(LocalDateTime.now())
                .build();

        loginHistoryRepository.save(history);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAnonymousAttempt(String username, RequestInfoUtils.RequestInfo requestInfo) {
        LoginHistory history = LoginHistory.builder()
                .user(null)
                .attemptedUsername(username)
                .ipAddress(requestInfo.getIpAddress())
                .userAgent(requestInfo.getUserAgent())
                .browser(requestInfo.getBrowser())
                .os(requestInfo.getOs())
                .device(requestInfo.getDevice())
                .success(false)
                .failureReason("User not found")
                .timestamp(LocalDateTime.now())
                .build();

        loginHistoryRepository.save(history);
    }
}
