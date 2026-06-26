package com.abhisheksingh.loanaxisapi.feature.audit.annotation;

import com.abhisheksingh.loanaxisapi.feature.audit.enums.AuditEventType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    AuditEventType eventType();
    String resource() default "";
}
