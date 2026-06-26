package com.abhisheksingh.loanaxisapi.feature.admin.service;

import com.abhisheksingh.loanaxisapi.feature.admin.dto.GetAdminUsersResponse;
import com.abhisheksingh.loanaxisapi.feature.audit.annotation.Auditable;
import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import com.abhisheksingh.loanaxisapi.feature.user.enums.District;
import com.abhisheksingh.loanaxisapi.feature.user.enums.UserRole;
import com.abhisheksingh.loanaxisapi.feature.user.exception.UserNotFoundException;
import com.abhisheksingh.loanaxisapi.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<GetAdminUsersResponse> getAdminUsers(
            String search,
            UserRole role,
            District district,
            Boolean accountLocked,
            Integer page,
            Integer size,
            String sortBy,
            String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return userRepository.searchAdminUsers(
                search,
                role,
                district,
                accountLocked,
                pageable
        ).map(this::mapToAdminUsersResponse);
    }

    private GetAdminUsersResponse mapToAdminUsersResponse(User user) {
        GetAdminUsersResponse response = new GetAdminUsersResponse();

        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFullName(user.getFullName());
        response.setRole(user.getRole());
        response.setDistrict(user.getDistrict());
        response.setAccountLocked(user.getAccountLocked());
        response.setFailedLoginAttempts(user.getFailedLoginAttempts());
        response.setLockedUntil(user.getLockedUntil());

        return response;
    }

    @Transactional
    @Auditable(eventType = AuditEventType.USER_LOCKED)
    public void lockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setAccountLocked(true);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    @Transactional
    @Auditable(eventType = AuditEventType.USER_UNLOCKED)
    public void unlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);

        userRepository.save(user);
    }

    @Transactional
    @Auditable(eventType = AuditEventType.FORCE_LOGOUT)
    public void forceLogoutUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.setTokensInvalidatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
