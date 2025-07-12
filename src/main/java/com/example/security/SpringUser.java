package com.example.security;

import com.example.dto.jwt.JwtToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.Instant;


public class SpringUser extends User {

    private final JwtToken token;


    public SpringUser(JwtToken token) {
        super(token.username(), "", true, true, token.expiresAt().isAfter(Instant.now()), true,
              token.authorities().stream()
                      .map(SimpleGrantedAuthority::new)
                      .toList());
        this.token = token;
    }

    //    public SpringUser(String username, String password, boolean enabled, boolean accountNonExpired,
    //                      boolean credentialsNonExpired, boolean accountNonLocked,
    //                      Collection<? extends GrantedAuthority> authorities, JwtToken token) {
    //        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    //        this.token = token;
    //    }

    public Long getId() {
        return token.id();
    }
}