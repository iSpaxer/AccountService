package com.example.util.annotation;

import com.example.util.validator.CredentialsUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CredentialsUserValidator.class)
public @interface ValidCredentialsUser {
    String message() default "Invalid credentials state";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
