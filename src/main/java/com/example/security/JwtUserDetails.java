package com.example.security;

import com.example.entity.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtUserDetails extends User implements UserDetails {

    public JwtUserDetails(Long id, String username, @NotNull String password) {
        super(id, username, password);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

}
