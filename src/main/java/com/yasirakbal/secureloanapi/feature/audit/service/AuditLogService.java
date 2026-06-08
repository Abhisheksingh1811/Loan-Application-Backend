package com.yasirakbal.secureloanapi.feature.audit.service;

import com.yasirakbal.secureloanapi.feature.audit.entity.SecurityAuditLog;
import com.yasirakbal.secureloanapi.feature.audit.repository.AuditLogRepository;
import com.yasirakbal.secureloanapi.feature.audit.specification.AuditLogSpecification;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class AuditLogService {
    private AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public Page<SecurityAuditLog> getAllAuditLogs(
            Long userId,
            String action,
            String resource,
            String httpMethod,
            String ipAddress,
            String userAgent,
            Boolean success,
            LocalDateTime timestampFrom,
            LocalDateTime timestampTo,
            Integer page,
            Integer size,
            String sortBy,
            String direction) {

        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Specification<SecurityAuditLog> spec = AuditLogSpecification.filterBy(
                userId, action, resource, httpMethod, ipAddress, userAgent, success, timestampFrom, timestampTo
        );

        return auditLogRepository.findAll(spec, pageable);
    }
}
