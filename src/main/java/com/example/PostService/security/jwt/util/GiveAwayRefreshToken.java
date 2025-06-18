package com.example.PostService.security.jwt.util;


import com.example.PostService.dto.jwt.JwtToken;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Function;

public class GiveAwayRefreshToken implements Function<JwtToken, Boolean> {

    private final int standard_for_issuing_percent = 50;

    @Override
    public Boolean apply(JwtToken jwtToken) {
        // дефолтное время
        var defaultBetween = Duration.between(jwtToken.expiresAt(), jwtToken.createdAt());

        // время когда мы можем выдать новый refreshTOken
        var timeOfIssue = 0L;

        // время которое прошло
        var timeLeft = Duration.between(jwtToken.expiresAt(), Instant.now());

        return timeLeft.toMillis() > timeOfIssue;
    }
}
