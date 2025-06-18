package com.example.PostService.security.converter;

import com.example.PostService.dto.jwt.JwtToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import java.util.function.Function;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessJwtAuthenticationConverter implements AuthenticationConverter {

    Function<String, JwtToken> accessTokenStringDeserializer;

    Function<String, JwtToken> refreshTokenStringDeserializer;

    @Override
    public Authentication convert(HttpServletRequest request) {
        var authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            var token = authorization.replace("Bearer ", "");
            var accessToken = this.accessTokenStringDeserializer.apply(token);
            if (accessToken != null) {
                return new PreAuthenticatedAuthenticationToken(accessToken, token);
            }

            var refreshToken = this.refreshTokenStringDeserializer.apply(token);
            if (refreshToken != null) {
                return new PreAuthenticatedAuthenticationToken(refreshToken, token);
            }
        }
//        throw new AccessDeniedException("Отсутсвует access токен");
        return null;
    }


}
