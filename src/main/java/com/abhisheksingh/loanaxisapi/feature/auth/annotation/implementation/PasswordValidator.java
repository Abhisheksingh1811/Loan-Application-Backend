package com.abhisheksingh.loanaxisapi.feature.auth.annotation.implementation;

import com.abhisheksingh.loanaxisapi.feature.auth.annotation.interfaces.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator
        implements ConstraintValidator<ValidPassword, String> {
    //Minimum 8 and maximum 32 characters, at least one uppercase letter, one lowercase letter, one number and one special character:
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,32}$";

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context){
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
