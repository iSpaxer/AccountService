package com.example.security.jwt.factory;

import com.example.dto.jwt.JwtToken;

import java.util.function.Function;

public interface JwtAccessTokenFactory extends Function<JwtToken, JwtToken> {
}
