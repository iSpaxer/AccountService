package com.example.PostService.dto.jwt;

public record JwtResponse(String accessToken, String expiryAccessToken,
                          String refreshToken, String expiryRefreshToken) {
}
