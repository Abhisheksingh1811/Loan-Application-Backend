package com.abhisheksingh.loanaxisapi.common.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(
                String.format("%s not found with id: %s", resourceName, id),
                HttpStatus.NOT_FOUND
        );
        addDetail("resourceName", resourceName);
        addDetail("resourceId", id);
    }

    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
