package com.example.security.jwt.factory;

import com.example.dto.jwt.JwtResponse;
import com.example.dto.jwt.JwtToken;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationJwtResponseMapper implements Function<Authentication, JwtResponse> {

    Function<Authentication, JwtToken> jwtRefreshFactory;
    Function<JwtToken, JwtToken> jwtAccessFactory;
    Function<JwtToken, String> accessTokenSerializer;
    Function<JwtToken, String> refreshTokenSerializer;

    @Override
    public JwtResponse apply(Authentication authentication) {
        var refreshToken = jwtRefreshFactory.apply(authentication);
        var accessToken = jwtAccessFactory.apply(refreshToken);
        return new JwtResponse(
                accessTokenSerializer.apply(accessToken), accessToken.expiresAt().toString(),
                refreshTokenSerializer.apply(refreshToken), refreshToken.expiresAt().toString()
        );
    }

}
