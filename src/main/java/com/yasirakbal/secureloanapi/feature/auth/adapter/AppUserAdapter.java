package com.yasirakbal.secureloanapi.feature.auth.adapter;

import com.yasirakbal.secureloanapi.feature.user.entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class AppUserAdapter implements UserDetails {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().getValue()));
    }

    @Override
    public @Nullable String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        if (user.getPasswordExpired()) return false;

        LocalDateTime reference = user.getPasswordChangedAt() != null
                ? user.getPasswordChangedAt()
                : user.getCreatedAt();

        return ChronoUnit.DAYS.between(reference, LocalDateTime.now()) < 90;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }
}
