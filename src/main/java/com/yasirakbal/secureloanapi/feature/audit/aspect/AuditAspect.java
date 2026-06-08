package com.yasirakbal.secureloanapi.feature.audit.aspect;

import com.yasirakbal.secureloanapi.feature.audit.annotation.Auditable;
import com.yasirakbal.secureloanapi.feature.audit.entity.SecurityAuditLog;
import com.yasirakbal.secureloanapi.feature.audit.repository.AuditLogRepository;
import com.yasirakbal.secureloanapi.feature.user.entity.User;
import com.yasirakbal.secureloanapi.feature.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ExpressionParser parser = new SpelExpressionParser();

    private static final Pattern SPEL_VARIABLE_PATTERN = Pattern.compile("#\\{?([a-zA-Z_][a-zA-Z0-9_.]*)\\}?");

    @AfterReturning(pointcut = "@annotation(auditable)", returning = "result")
    public void auditSuccess(JoinPoint joinPoint, Auditable auditable, Object result) {
        try {
            User user = getCurrentUser();
            String resource = extractResource(joinPoint, auditable, result);

            SecurityAuditLog auditLog = SecurityAuditLog.builder()
                    .user(user)
                    .eventType(auditable.eventType())
                    .resource(resource)
                    .success(true)
                    .occurredAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.info("📝 Audit: {} - {} by {}",
                    auditable.eventType(),
                    resource,
                    user != null ? user.getUsername() : "anonymous");

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    @AfterThrowing(pointcut = "@annotation(auditable)", throwing = "error")
    public void auditFailure(JoinPoint joinPoint, Auditable auditable, Throwable error) {
        try {
            User user = getCurrentUser();
            String resource = extractResource(joinPoint, auditable, null);

            SecurityAuditLog auditLog = SecurityAuditLog.builder()
                    .user(user)
                    .eventType(auditable.eventType())
                    .resource(resource)
                    .success(false)
                    .failureReason(error.getMessage())
                    .occurredAt(LocalDateTime.now())
                    .build();

            auditLogRepository.saveInNewTransaction(auditLog);

        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            Long userId = jwt.getClaim("userId");
            return userRepository.findById(userId).orElse(null);
        }
        return null;
    }

    private String extractResource(JoinPoint joinPoint, Auditable auditable, Object result) {
        String resourceExpression = auditable.resource();

        // 1. SpEL değişken referansı varsa (#variable içeriyorsa)
        if (!resourceExpression.isEmpty() && resourceExpression.contains("#")) {
            return resolveVariables(resourceExpression, joinPoint, result);
        }

        // 2. Sabit string varsa
        if (!resourceExpression.isEmpty()) {
            return resourceExpression;
        }

        // 3. Otomatik: İlk parametreyi al
        Object[] args = joinPoint.getArgs();
        if (args.length > 0 && args[0] != null) {
            return args[0].toString();
        }

        return "unknown";
    }

    private String resolveVariables(String expression, JoinPoint joinPoint, Object result) {
        try {
            StandardEvaluationContext context = buildEvaluationContext(joinPoint, result);

            Matcher matcher = SPEL_VARIABLE_PATTERN.matcher(expression);
            StringBuilder resolved = new StringBuilder();

            while (matcher.find()) {
                String fullMatch = matcher.group(0);
                Object value = parser.parseExpression(fullMatch).getValue(context);
                matcher.appendReplacement(resolved, value != null ? Matcher.quoteReplacement(value.toString()) : "null");
            }
            matcher.appendTail(resolved);

            return resolved.toString();

        } catch (Exception e) {
            log.error("Failed to resolve variables in expression: {}", expression, e);
            return "error:" + expression;
        }
    }

    private StandardEvaluationContext buildEvaluationContext(JoinPoint joinPoint, Object result) {
        StandardEvaluationContext context = new StandardEvaluationContext();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] paramValues = joinPoint.getArgs();

        for (int i = 0; i < paramNames.length; i++) {
            context.setVariable(paramNames[i], paramValues[i]);
        }

        if (result != null) {
            context.setVariable("result", result);
        }

        return context;
    }
}