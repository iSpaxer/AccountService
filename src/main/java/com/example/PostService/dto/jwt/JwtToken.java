package com.example.PostService.dto.jwt;

import java.time.Instant;
import java.util.List;

public record JwtToken(String username, List<String> authorities,
                       Instant createdAt, Instant expiresAt) {
}
