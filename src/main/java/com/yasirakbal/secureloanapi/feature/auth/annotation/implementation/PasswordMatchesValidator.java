package com.yasirakbal.secureloanapi.feature.auth.annotation.implementation;

import com.yasirakbal.secureloanapi.feature.auth.annotation.interfaces.PasswordMatches;
import com.yasirakbal.secureloanapi.feature.auth.dto.UserRegisterRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, UserRegisterRequest> {

    @Override
    public boolean isValid(UserRegisterRequest request, ConstraintValidatorContext context){
        return request.getPassword().equals(request.getMatchingPassword());
    }
}