package com.yasirakbal.secureloanapi.security.filter;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.yasirakbal.secureloanapi.common.exception.ErrorResponse;
import com.yasirakbal.secureloanapi.feature.blacklist.service.JwtBlacklistService;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import com.yasirakbal.secureloanapi.security.exception.AccessTokenRequiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
@RequiredArgsConstructor
public class JwtBlacklistFilter extends OncePerRequestFilter {

    private final JwtBlacklistService jwtBlacklistService;
    private final JwtDecoder jwtDecoder;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        ObjectMapper objectMapper = new ObjectMapper();

        if (!jwtBlacklistService.isBlacklisted(token)) {
            Jwt jwt = jwtDecoder.decode(token);
            Long userId = jwt.getClaim("userId");
            Instant issuedAt = jwt.getIssuedAt();
            User user = userRepository.findById(userId).orElse(null);
            if (user != null && user.getAccountLocked()) {
                accountLocked(response, objectMapper);
                return;
            }

            LocalDateTime tokenIssuedAt = LocalDateTime.ofInstant(issuedAt, ZoneOffset.UTC);
            if(user != null && user.getTokensInvalidatedAt() != null && tokenIssuedAt.isBefore(user.getTokensInvalidatedAt())) {
                tokenRevoked(response, objectMapper);
                return;
            }

            filterChain.doFilter(request, response);
            return;
        }

        tokenRevoked(response, objectMapper);
    }

    private void tokenRevoked(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(401)
                .error("Unauthorized")
                .message("Token invalidated. Please login again.")
                .build();

        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }

    private void accountLocked(HttpServletResponse response, ObjectMapper objectMapper) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(403)
                .error("Forbidden")
                .message("Your account has been locked by an administrator")
                .build();

        objectMapper.writeValue(response.getWriter(), error);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/login")
                || path.startsWith("/api/auth/register")
                || path.startsWith("/api/auth/refresh");
    }
}