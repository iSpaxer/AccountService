package com.example.security.jwt.factory;


import com.example.dto.jwt.JwtToken;
import com.example.security._static.SecureStatic;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;

/**
 * Создание из Refresh -> Access
 */
public class DefaultJwtAccessTokenFactory implements JwtAccessTokenFactory {

    Duration tokenTtl = Duration.ofMinutes(5);

    @Override
    public JwtToken apply(JwtToken token) {
        var authorities = new LinkedList<String>();
        token.authorities()
                .stream()
                .filter(authority -> authority.startsWith(SecureStatic.PREFIX_FOR_AUTHORITIES))
                .map(authority -> authority.substring(SecureStatic.PREFIX_FOR_AUTHORITIES.length()))
                .forEach(authorities::add);
        var now = Instant.now();
        return new JwtToken(token.id(), authorities, now, now.plus(tokenTtl));
    }

}
