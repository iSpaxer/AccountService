package com.example.configuration;

import com.example.security.JwtUserDetailsService;
import com.example.security.jwt.deserializer.AccessTokenJwsDeserializer;
import com.example.security.jwt.deserializer.RefreshTokenJweDeserializer;
import com.example.security.jwt.factory.DefaultJwtAccessTokenFactory;
import com.example.security.jwt.factory.DefaultJwtRefreshTokenFactory;
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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.text.ParseException;


@Configuration
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationConfigurer jwtAuthenticationConfigurer(
            @Value("${jwt.access-token-key}") String accessTokenKey,
            @Value("${jwt.refresh-token-key}") String refreshTokenKey,
            JwtUserDetailsService jwtUserDetailsService,
            PasswordEncoder passwordEncoder,
            HandlerExceptionResolver handlerExceptionResolver,
            ObjectMapper objectMapper,
            ApplicationDataComponent applicationDataComponent
    ) throws ParseException, JOSEException {
        return new JwtAuthenticationConfigurer(
                jwtUserDetailsService,
                passwordEncoder,
                new DefaultJwtRefreshTokenFactory(),
                new DefaultJwtAccessTokenFactory(),
                new AccessTokenJwsSerializer(new MACSigner(OctetSequenceKey.parse(accessTokenKey))),
                new RefreshTokenJweSerializer(new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey))),
                new AccessTokenJwsDeserializer(new MACVerifier(OctetSequenceKey.parse(accessTokenKey))),
                new RefreshTokenJweDeserializer(new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey))),
                handlerExceptionResolver,
                objectMapper,
                applicationDataComponent
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain chainAPI(HttpSecurity http,
                                        ApplicationDataComponent appData,
                                        JwtAuthenticationConfigurer jwtAuthenticationConfigurer) throws Exception {
        http
                .apply(jwtAuthenticationConfigurer);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/info").permitAll()
                        .requestMatchers(HttpMethod.GET, appData.glueEndpoints("/user", "/user/**")).permitAll()
                        .requestMatchers(appData.glueEndpoints("/user/create", "/user/restore")).anonymous()
                        .requestMatchers(appData.glueEndpoints("/user/{id:[1-9]\\d*}/**")).authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain chainAdmin(HttpSecurity http, ApplicationDataComponent appData) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher(appData.glueEndpoints("/admin/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(appData.glueEndpoints("/admin/**")).hasRole("ADMIN")
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain chainDefault(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/**").permitAll()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
