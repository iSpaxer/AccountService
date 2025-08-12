package com.example.dto.jwt;

import java.time.Instant;
import java.util.List;

public record JwtToken(Long id, String jti, List<String> authorities,
                       Instant createdAt, Instant expiresAt) {
}
