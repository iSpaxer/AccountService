package com.example.PostService.security.filter;

import com.example.PostService.dto.jwt.JwtResponse;
import com.example.PostService.dto.jwt.JwtToken;
import com.example.PostService.security.converter.RefreshJwtConverter;
import com.example.PostService.security.jwt.util.GiveAwayRefreshToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Function;

import static com.example.PostService.security.jwt.factory.DefaultJwtRefreshTokenFactory.REFRESH_TOKEN_Ttl;


@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtRefreshFilter extends OncePerRequestFilter {

    Function<String, JwtToken> refreshDeserializer;
    Function<JwtToken, JwtToken> jwtAccessFactory;
    Function<JwtToken, String> accessTokenSerializer;
    Function<JwtToken, String> refreshTokenSerializer;

    Function<JwtToken, Boolean> giveAwayRefresh = new GiveAwayRefreshToken();
    Function<HttpServletRequest, String> refreshJwtConverter = new RefreshJwtConverter();

    ObjectMapper objectMapper = new ObjectMapper();


    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/api/jwt/refresh", HttpMethod.POST.name());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (this.requestMatcher.matches(request)) {
            var refreshTokenStr = refreshJwtConverter.apply(request);
            var refreshToken = refreshDeserializer.apply(refreshTokenStr);

            if (refreshToken.expiresAt().isBefore(Instant.now())) {
                throw new JsonParseException("Время жизни refresh токена истекло. Перезайдите в аккаунт.");
            }

            JwtResponse jwtResponse = getJwtResponse(refreshToken, refreshTokenStr);

            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), jwtResponse);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @NotNull
    private JwtResponse getJwtResponse(JwtToken refreshToken, String refreshTokenStr) {
        var new_refreshToken = new JwtToken(
                refreshToken.username(),
                refreshToken.authorities(),
                Instant.now(), Instant.now().plus(REFRESH_TOKEN_Ttl));

        var new_accessToken = jwtAccessFactory.apply(new_refreshToken);

        JwtResponse jwtResponse;
        if (giveAwayRefresh.apply(refreshToken)) {
            jwtResponse = new JwtResponse(
                    accessTokenSerializer.apply(new_accessToken), new_accessToken.expiresAt().toString(),
                    refreshTokenSerializer.apply(new_refreshToken), new_refreshToken.expiresAt().toString()
            );
        } else {
            jwtResponse = new JwtResponse(
                    accessTokenSerializer.apply(new_accessToken), new_accessToken.expiresAt().toString(),
                    refreshTokenStr, refreshToken.expiresAt().toString()
            );
        }
        return jwtResponse;
    }

}

