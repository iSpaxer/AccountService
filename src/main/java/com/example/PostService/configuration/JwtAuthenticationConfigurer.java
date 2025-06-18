package com.example.PostService.configuration;

import com.example.PostService.dto.ExceptionBody;
import com.example.PostService.dto.jwt.JwtToken;
import com.example.PostService.security.JwtAuthenticationUserDetailsService;
import com.example.PostService.security.JwtUserDetailsService;
import com.example.PostService.security.converter.AccessJwtAuthenticationConverter;
import com.example.PostService.security.filter.JwtExceptionHandlerFilter;
import com.example.PostService.security.filter.JwtLoginFilter;
import com.example.PostService.security.filter.JwtRefreshFilter;
import com.example.PostService.security.jwt.factory.AuthenticationJwtResponseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@Builder
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationConfigurer extends AbstractHttpConfigurer<JwtAuthenticationConfigurer, HttpSecurity> {
    JwtUserDetailsService userDetailsService;
    PasswordEncoder passwordEncoder;

    Function<Authentication, JwtToken> jwtRefreshFactory;
    Function<JwtToken, JwtToken> jwtAccessFactory;

    Function<JwtToken, String> accessTokenSerializer;
    Function<JwtToken, String> refreshTokenSerializer;


    Function<String, JwtToken> accessTokenDeserializer;
    Function<String, JwtToken> refreshTokenDeserializer;

    HandlerExceptionResolver handlerExceptionResolver;
    ObjectMapper objectMapper;


    @Override
    public void configure(HttpSecurity builder) {
        var daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);

        var jwtLoginFilter = new JwtLoginFilter(
                daoAuthenticationProvider,
                AuthenticationJwtResponseMapper.builder()
                        .jwtRefreshFactory(jwtRefreshFactory)
                        .jwtAccessFactory(jwtAccessFactory)
                        .accessTokenSerializer(accessTokenSerializer)
                        .refreshTokenSerializer(refreshTokenSerializer)
                        .build()
        );

        var jwtRefreshFilter = new JwtRefreshFilter(
                refreshTokenDeserializer,
                jwtAccessFactory,
                accessTokenSerializer,
                refreshTokenSerializer
        );

        var jwtAuthenticationFilter = new AuthenticationFilter(
                builder.getSharedObject(AuthenticationManager.class),
                new AccessJwtAuthenticationConverter(accessTokenDeserializer, refreshTokenDeserializer)
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


        // todo переделать фильтр
        var authenticationProvider = new PreAuthenticatedAuthenticationProvider();
        authenticationProvider.setPreAuthenticatedUserDetailsService(
                new JwtAuthenticationUserDetailsService());

        builder
                .addFilterAfter(jwtLoginFilter, BasicAuthenticationFilter.class)
                .addFilterAfter(jwtRefreshFilter, JwtLoginFilter.class)
                .addFilterBefore(new JwtExceptionHandlerFilter(handlerExceptionResolver, objectMapper), JwtLoginFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, CsrfFilter.class)
                .authenticationProvider(authenticationProvider)
                .authenticationProvider(daoAuthenticationProvider);

    }

}
