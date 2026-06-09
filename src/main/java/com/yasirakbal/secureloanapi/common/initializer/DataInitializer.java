package com.yasirakbal.secureloanapi.common.initializer;

import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.enums.UserRole;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.yasirakbal.secureloanapi.feature.user.enums.District;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        List<User> users = List.of(
                createUser("johndoe", "Pass123!", "john@example.com", "John Doe",
                        "12345678901", "+905551234567", LocalDate.of(1965, 5, 15),
                        new BigDecimal("25000.00"), 720, UserRole.CUSTOMER,District.HYDERABAD),

                createUser("AbhishekSingh", "Pass123!", "abhishsingh202@gmail.com", "Abhishek Singh",
                        "98765432109", "+905559876543", LocalDate.of(1988, 3, 20),
                        new BigDecimal("35000.00"), 780, UserRole.CUSTOMER,District.WARANGAL),

                createUser("creditofficer", "Pass123!", "officer@secureloan.com", "Credit Officer",
                        "11111111111", "+905550000001", LocalDate.of(1985, 6, 10),
                        new BigDecimal("60000.00"), 800, UserRole.CREDIT_OFFICER,District.WARANGAL),

                createUser("admin", "Pass123!", "admin@secureloan.com", "Admin User",
                        "22222222222", "+905550000000", LocalDate.of(1980, 1, 1),
                        new BigDecimal("80000.00"), 850, UserRole.ADMIN,District.WARANGAL),

                createLockedUser("lockeduser", "Pass123!", "locked@example.com", "Locked User",
                        "33333333333", "+905553333333", LocalDate.of(1992, 11, 25),
                        new BigDecimal("20000.00"), 650, UserRole.CUSTOMER,District.WARANGAL)
        );

        userRepository.saveAll(users);
    }

    private User createUser(String username, String password, String email, String fullName,
                            String identityNumber, String phoneNumber, LocalDate birthDate,
                            BigDecimal monthlyIncome, Integer creditScore, UserRole role,District district) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullName(fullName)
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .monthlyIncome(monthlyIncome)
                .creditScore(creditScore)
                .role(role)
                .enabled(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .passwordExpired(false)
                .district(district)
                .build();
    }

    private User createLockedUser(String username, String password, String email, String fullName,
                                  String identityNumber, String phoneNumber, LocalDate birthDate,
                                  BigDecimal monthlyIncome, Integer creditScore, UserRole role,District district) {
        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .email(email)
                .fullName(fullName)
                .identityNumber(identityNumber)
                .phoneNumber(phoneNumber)
                .birthDate(birthDate)
                .monthlyIncome(monthlyIncome)
                .creditScore(creditScore)
                .role(role)
                .district(district)
                .enabled(true)
                .accountLocked(true)
                .failedLoginAttempts(5)
                .lockedUntil(LocalDateTime.now().plusHours(1))
                .passwordExpired(false)
                .build();
    }
}