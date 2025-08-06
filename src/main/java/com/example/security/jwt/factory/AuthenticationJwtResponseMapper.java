package com.example.security.jwt.factory;

import com.example.dto.jwt.JwtResponse;
import com.example.dto.jwt.JwtToken;
import com.example.service.JwtRedisService;
import lombok.Builder;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

@Builder
public class AuthenticationJwtResponseMapper implements Function<Authentication, JwtResponse> {

    private final Function<Authentication, JwtToken> jwtRefreshFactory;
    private final Function<JwtToken, JwtToken> jwtAccessFactory;
    private final Function<JwtToken, String> accessTokenSerializer;
    private final Function<JwtToken, String> refreshTokenSerializer;
    private final JwtRedisService jwtRedisService;


    @Override
    public JwtResponse apply(Authentication authentication) {
        JwtToken refreshToken = jwtRefreshFactory.apply(authentication);
        JwtToken accessToken = jwtAccessFactory.apply(refreshToken);

        jwtRedisService.signIn(refreshToken);

        return new JwtResponse(
                accessTokenSerializer.apply(accessToken), accessToken.expiresAt().toString(),
                refreshTokenSerializer.apply(refreshToken), refreshToken.expiresAt().toString()
        );
    }

}
