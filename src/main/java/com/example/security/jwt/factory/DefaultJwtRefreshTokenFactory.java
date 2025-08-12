package com.example.security.jwt.factory;

import com.example.dto.jwt.JwtToken;
import com.example.security._static.SecureStatic;
import com.example.security.auth.AuthPrincipalAbstractIdentifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


public class DefaultJwtRefreshTokenFactory implements JwtRefreshTokenFactory {

    public static Duration REFRESH_TOKEN_Ttl = Duration.ofDays(30);


    @Override
    public JwtToken apply(Authentication authentication) {

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthPrincipalAbstractIdentifier authPrincipalAbstractIdentifier)) {
            throw new IllegalArgumentException("Principal is not an instance of CustomUserDetails");
        }


        var authorities = new ArrayList<>(List.of("JWT_REFRESH", "JWT_LOGOUT"));
        authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> SecureStatic.PREFIX_FOR_AUTHORITIES + authority)
                .forEach(authorities::add);
        var now = Instant.now();
        return new JwtToken(authPrincipalAbstractIdentifier.getId(), authPrincipalAbstractIdentifier.getJti(),
                            authorities, now,
                            now.plus(REFRESH_TOKEN_Ttl));
    }

}
