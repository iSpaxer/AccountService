package com.example.security.filter;

import com.example.dto.jwt.JwtResponse;
import com.example.dto.jwt.JwtToken;
import com.example.security.converter.RefreshJwtConverter;
import com.example.security.jwt.factory.DefaultJwtRefreshTokenFactory;
import com.example.security.jwt.util.GiveAwayRefreshToken;
import com.example.service.JwtRedisService;
import com.example.util.ApplicationDataComponent;
import com.example.util.exception.ForbiddenException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.JsonParseException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.function.Function;


public class JwtRefreshFilter extends OncePerRequestFilter {

    private final Function<String, JwtToken> refreshDeserializer;
    private final Function<JwtToken, JwtToken> jwtAccessFactory;
    private final Function<JwtToken, String> accessTokenSerializer;
    private final Function<JwtToken, String> refreshTokenSerializer;
    private final Function<JwtToken, Boolean> giveAwayRefresh = new GiveAwayRefreshToken();
    private final Function<HttpServletRequest, String> refreshJwtConverter = new RefreshJwtConverter();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RequestMatcher requestMatcher;
    private final JwtRedisService jwtRedisService;

    public JwtRefreshFilter(ApplicationDataComponent dataComponent, Function<String, JwtToken> refreshDeserializer,
                            Function<JwtToken, JwtToken> jwtAccessFactory,
                            Function<JwtToken, String> accessTokenSerializer,
                            Function<JwtToken, String> refreshTokenSerializer, JwtRedisService jwtRedisService) {
        this.refreshDeserializer = refreshDeserializer;
        this.jwtAccessFactory = jwtAccessFactory;
        this.accessTokenSerializer = accessTokenSerializer;
        this.refreshTokenSerializer = refreshTokenSerializer;
        this.requestMatcher = new AntPathRequestMatcher(dataComponent.glueEndpoint("/jwt/refresh"),
                                                        HttpMethod.POST.name());
        this.jwtRedisService = jwtRedisService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (this.requestMatcher.matches(request)) {
            String refreshTokenStr = refreshJwtConverter.apply(request);
            JwtToken refreshToken = refreshDeserializer.apply(refreshTokenStr);

            if (refreshToken.expiresAt().isAfter(Instant.now())) {
                if (jwtRedisService.checkForAccess(refreshToken)) {
                    JwtResponse jwtResponse = getJwtResponse(refreshToken, refreshTokenStr);
                    response.setStatus(HttpStatus.OK.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getWriter(), jwtResponse);
                    return;
                }
                throw new ForbiddenException("Token is not valid");
            }
            throw new JsonParseException("The refresh token's lifetime has expired. Log back into your account.");
        }
        filterChain.doFilter(request, response);
    }

    @NotNull
    private JwtResponse getJwtResponse(JwtToken refreshToken, String refreshTokenStr) {
        // новому refresh назначаем старый Jti
        var new_refreshToken = new JwtToken(
                refreshToken.id(),
                refreshToken.jti(),

                refreshToken.authorities(),
                Instant.now(), Instant.now().plus(DefaultJwtRefreshTokenFactory.REFRESH_TOKEN_Ttl));

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

