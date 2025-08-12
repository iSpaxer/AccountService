package com.example.util.exception;

import org.springframework.security.core.AuthenticationException;

public class ForbiddenException extends AuthenticationException {
    public ForbiddenException(String accessIsDenied) {
        super(accessIsDenied);
    }
}
