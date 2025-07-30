package com.example.configuration;

import com.example.security.JwtUserDetailsService;
import com.example.security.jwt.deserializer.AccessTokenJwsDeserializer;
import com.example.security.jwt.deserializer.RefreshTokenJweDeserializer;
import com.example.security.jwt.factory.*;
import com.example.security.jwt.serializer.AccessTokenJwsSerializer;
import com.example.security.jwt.serializer.RefreshTokenJweSerializer;
import com.example.util.ApplicationDataComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.text.ParseException;

@Configuration
public class JwtCommonConfig {

    private final JwtRefreshTokenFactory jwtRefreshTokenFactory;
    private final JwtAccessTokenFactory jwtAccessTokenFactory;
    private final AccessTokenJwsSerializer accessTokenJwsSerializer;
    private final RefreshTokenJweSerializer refreshTokenJweSerializer;
    private final AccessTokenJwsDeserializer accessTokenJwsDeserializer;
    private final RefreshTokenJweDeserializer refreshTokenJweDeserializer;

    public JwtCommonConfig(
            @Value("${jwt.access-token-key}") String accessTokenKey,
            @Value("${jwt.refresh-token-key}") String refreshTokenKey) throws ParseException, JOSEException {
        this.jwtRefreshTokenFactory = new DefaultJwtRefreshTokenFactory();
        this.jwtAccessTokenFactory = new DefaultJwtAccessTokenFactory();
        this.accessTokenJwsSerializer = new AccessTokenJwsSerializer(
                new MACSigner(OctetSequenceKey.parse(accessTokenKey)));
        this.refreshTokenJweSerializer = new RefreshTokenJweSerializer(
                new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey)));
        this.accessTokenJwsDeserializer = new AccessTokenJwsDeserializer(
                new MACVerifier(OctetSequenceKey.parse(accessTokenKey)));
        this.refreshTokenJweDeserializer = new RefreshTokenJweDeserializer(
                new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey)));
    }

    @Bean
    public AuthenticationJwtResponseMapper authenticationJwtResponseMapper() {
        return AuthenticationJwtResponseMapper.builder()
                .jwtRefreshFactory(jwtRefreshTokenFactory)
                .jwtAccessFactory(jwtAccessTokenFactory)
                .accessTokenSerializer(accessTokenJwsSerializer)
                .refreshTokenSerializer(refreshTokenJweSerializer)
                .build();
    }

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            JwtUserDetailsService jwtUserDetailsService,
            PasswordEncoder passwordEncoder,
            HandlerExceptionResolver handlerExceptionResolver,
            ObjectMapper objectMapper,
            ApplicationDataComponent applicationDataComponent,
            AuthenticationJwtResponseMapper authenticationJwtResponseMapper) {
        return new JwtAuthenticationConfigurer(
                jwtUserDetailsService,
                passwordEncoder,
                jwtRefreshTokenFactory,
                jwtAccessTokenFactory,
                accessTokenJwsSerializer,
                refreshTokenJweSerializer,
                accessTokenJwsDeserializer,
                refreshTokenJweDeserializer,
                handlerExceptionResolver,
                objectMapper,
                applicationDataComponent,
                authenticationJwtResponseMapper
        );
    }


}
