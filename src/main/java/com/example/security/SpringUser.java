package com.example.security;

import com.example.dto.jwt.JwtToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;


public class SpringUser extends User {

    private final JwtToken token;

    public Long getId() {
        return token.id();
    }

    public SpringUser(String username, String password, Collection<? extends GrantedAuthority> authorities, JwtToken token) {
        super(username, password, authorities);
        this.token = token;
    }

    public SpringUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, JwtToken token) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.token = token;
    }

    public JwtToken getToken() {
        return token;
    }
}