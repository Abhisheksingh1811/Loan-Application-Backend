package com.yasirakbal.secureloanapi.feature.audit.repository;

import com.yasirakbal.secureloanapi.feature.audit.entity.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
    List<LoginHistory> findLoginHistoriesByUserId(Long userId);
}
