package com.yasirakbal.secureloanapi.feature.audit.specification;

import com.yasirakbal.secureloanapi.feature.audit.entity.SecurityAuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuditLogSpecification {
    public static Specification<SecurityAuditLog> filterBy(
            Long userId,
            String action,
            String resource,
            String httpMethod,
            String ipAddress,
            String userAgent,
            Boolean success,
            LocalDateTime timestampFrom,
            LocalDateTime timestampTo
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(userId != null) {
                predicates.add(
                    criteriaBuilder.equal(root.get("userId"), userId)
                );
            }

            if(action != null && !action.isBlank()) {
                predicates.add(
                  criteriaBuilder.like(
                          criteriaBuilder.lower(root.get("action")),
                          "%" + action.toLowerCase() + "%"
                  )
                );
            }

            if(resource != null && !resource.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("resource")),
                                "%" + resource.toLowerCase() + "%"
                        )
                );
            }

            if(httpMethod != null && !httpMethod.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("httpMethod")),
                                "%" + httpMethod.toLowerCase() + "%"
                        )
                );
            }

            if(ipAddress != null && !ipAddress.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                root.get("ipAddress"),
                                "%" + ipAddress + "%"
                        )
                );
            }

            if(userAgent != null && !userAgent.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("userAgent")),
                                "%" + userAgent.toLowerCase() + "%"
                        )
                );
            }

            if(success != null) {
                predicates.add(
                        criteriaBuilder.equal(root.get("success"), success)
                );
            }

            if(timestampFrom != null) {
                predicates.add(
                  criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), timestampFrom)
                );
            }

            if(timestampTo != null) {
                predicates.add(
                        criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), timestampTo)
                );
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
