package com.yasirakbal.secureloanapi.feature.audit.repository;

import com.yasirakbal.secureloanapi.feature.audit.entity.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AuditLogRepository extends JpaRepository<SecurityAuditLog, Long>, JpaSpecificationExecutor<SecurityAuditLog> {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    default SecurityAuditLog saveInNewTransaction(SecurityAuditLog auditLog) {
        return save(auditLog);
    }
}
