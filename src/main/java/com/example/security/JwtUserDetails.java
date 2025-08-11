package com.example.security;

import com.example.entity.User;
import com.example.security.auth.AuthPrincipalAbstractIdentifier;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class JwtUserDetails extends User implements UserDetails, AuthPrincipalAbstractIdentifier {

    private final String jti;

    public JwtUserDetails(Long id, String username, @NotNull String password, String jti) {
        super(id, username, password);
        this.jti = jti;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }


    @Override
    public String getJti() {
        return jti;
    }
}
