package com.example.security;

import com.example.dto.jwt.JwtToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;


public class DefaultAuthenticationPrincipal extends User {

    private final JwtToken token;


    public DefaultAuthenticationPrincipal(JwtToken token) {
        super(token.id().toString(), "", true, true, token.expiresAt().isAfter(Instant.now()), true,
              token.authorities().stream()
                      .map(SimpleGrantedAuthority::new)
                      .toList());
        this.token = token;
    }

    public Long getId() {
        return token.id();
    }
}