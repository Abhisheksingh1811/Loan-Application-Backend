package com.abhisheksingh.loanaxisapi.feature.auth.service;

import com.abhisheksingh.loanaxisapi.feature.auth.adapter.AppUserAdapter;
import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import com.abhisheksingh.loanaxisapi.feature.user.repository.UserRepository;
import com.abhisheksingh.loanaxisapi.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getAccountLocked() && user.getLockedUntil() != null
                && LocalDateTime.now().isAfter(user.getLockedUntil())) {
            userService.unlockAccount(user.getId());
            user = userRepository.findById(user.getId()).orElseThrow();
        }

        return new AppUserAdapter(user);
    }
}
