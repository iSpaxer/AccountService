package com.example.configuration;

import com.example.dto.ExceptionBody;
import com.example.dto.jwt.JwtToken;
import com.example.security.JwtAuthenticationUserDetailsService;
import com.example.security.JwtUserDetailsService;
import com.example.security.converter.AccessJwtAuthenticationConverter;
import com.example.security.filter.JwtExceptionHandlerFilter;
import com.example.security.filter.JwtLoginFilter;
import com.example.security.filter.JwtLogoutFilter;
import com.example.security.filter.JwtRefreshFilter;
import com.example.security.jwt.factory.AuthenticationJwtResponseMapper;
import com.example.service.JwtRedisService;
import com.example.util.ApplicationDataComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFilter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.nio.charset.StandardCharsets;
import java.util.function.Function;

/**
 * Конфигурация JWT фильтров для Spring Security
 */
@RequiredArgsConstructor
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {
    private final JwtUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    private final Function<Authentication, JwtToken> jwtRefreshFactory;
    private final Function<JwtToken, JwtToken> jwtAccessFactory;

    private final Function<JwtToken, String> accessTokenSerializer;
    private final Function<JwtToken, String> refreshTokenSerializer;


    private final Function<String, JwtToken> accessTokenDeserializer;
    private final Function<String, JwtToken> refreshTokenDeserializer;

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final ObjectMapper objectMapper;
    private final ApplicationDataComponent dataComponent;
    private final AuthenticationJwtResponseMapper authenticationJwtResponseMapper;

    private final JwtRedisService jwtRedisService;

    @Override
    public void configure(HttpSecurity builder) {
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        var jwtLoginFilter = new JwtLoginFilter(
                dataComponent,
                daoAuthenticationProvider,
                authenticationJwtResponseMapper
        );

        var jwtRefreshFilter = new JwtRefreshFilter(
                dataComponent,
                refreshTokenDeserializer,
                jwtAccessFactory,
                accessTokenSerializer,
                refreshTokenSerializer
        );

        var jwtAuthenticationFilter = new AuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new AccessJwtAuthenticationConverter(accessTokenDeserializer, refreshTokenDeserializer, jwtRedisService)
        );

        jwtAuthenticationFilter
                .setFailureHandler((request, response, e) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                    response.getWriter().write(objectMapper.writeValueAsString(new ExceptionBody(e.getMessage())));
                });
        jwtAuthenticationFilter
                .setSuccessHandler((request, response, authentication) -> {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });


        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(
                new JwtAuthenticationUserDetailsService());

        var jwtLogoutFilter = new JwtLogoutFilter(dataComponent, jwtRedisService);

        builder
                .addFilterAfter(jwtLoginFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtRefreshFilter, JwtLoginFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(handlerExceptionResolver, objectMapper),
                                 JwtLoginFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, CsrfFilter.class)
                .addFilterAfter(jwtLogoutFilter, AuthenticationFilter.class)
                .authenticationProvider(authenticationProvider)
                .authenticationProvider(daoAuthenticationProvider);

    }

}
