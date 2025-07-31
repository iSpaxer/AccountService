package com.example.util.validator;

import com.example.entity.User;
import com.example.util.annotation.ValidCredentialsUser;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CredentialsUserValidator implements ConstraintValidator<ValidCredentialsUser, User> {
    @Override
    public boolean isValid(User user, ConstraintValidatorContext constraintValidatorContext) {
        return user == null;
    }
}
