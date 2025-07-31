package com.example.security.jwt.factory;

import com.example.dto.jwt.JwtToken;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

public interface JwtRefreshTokenFactory extends Function<Authentication, JwtToken> {
}
