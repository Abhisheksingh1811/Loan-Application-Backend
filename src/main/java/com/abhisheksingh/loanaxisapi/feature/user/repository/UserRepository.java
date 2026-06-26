package com.abhisheksingh.loanaxisapi.feature.user.repository;

import com.abhisheksingh.loanaxisapi.feature.user.entity.User;
import com.abhisheksingh.loanaxisapi.feature.user.enums.District;
import com.abhisheksingh.loanaxisapi.feature.user.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByEmail(String email);

    boolean existsUserByUsername(String username);

    boolean existsUserByIdentityNumber(String identityNumber);

    Optional<User> findUserByUsername(String username);

    @Query("""
            SELECT u
            FROM User u
            WHERE
                (
                    :search IS NULL OR :search = ''
                    OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :search, '%'))
                )
            AND (:role IS NULL OR u.role = :role)
            AND (:district IS NULL OR u.district = :district)
            AND (:accountLocked IS NULL OR u.accountLocked = :accountLocked)
            """)
    Page<User> searchAdminUsers(
            @Param("search") String search,
            @Param("role") UserRole role,
            @Param("district") District district,
            @Param("accountLocked") Boolean accountLocked,
            Pageable pageable
    );
}
