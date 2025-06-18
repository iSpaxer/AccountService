package com.example.security.jwt.factory;


import com.example.dto.jwt.JwtToken;
import com.example.security._static.SecureStatic;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.function.Function;


public class DefaultJwtAccessTokenFactory implements Function<JwtToken, JwtToken> {

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
        return new JwtToken(token.username(), authorities, now, now.plus(tokenTtl));
    }

}
