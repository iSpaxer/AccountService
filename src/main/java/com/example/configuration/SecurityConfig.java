package com.example.configuration;

import com.example.security.jwt.factory.AuthenticationJwtResponseMapper;
import com.example.security.oauth2.JwtOAuth2SuccessHandler;
import com.example.security.oauth2.OAuth2UserServiceImpl;
import com.example.util.ApplicationDataComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

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
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/info").permitAll()
                        .requestMatchers(HttpMethod.GET, appData.glueEndpoints("/user", "/user/**")).permitAll()
                        .requestMatchers(appData.glueEndpoints("/user/create", "/user/restore")).anonymous()
                        .requestMatchers(appData.glueEndpoints("/user", "/user/**")).authenticated()
                )
                .sessionManagement(sessionManagement ->
                                           sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain chainAdmin(HttpSecurity http, ApplicationDataComponent appData) throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
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
    public SecurityFilterChain chainDefault(HttpSecurity http, OAuth2UserServiceImpl oAuth2UserService,
                                            AuthenticationJwtResponseMapper authenticationJwtResponseMapper)
            throws Exception {
        http
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(new JwtOAuth2SuccessHandler(authenticationJwtResponseMapper))
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                )
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/**").permitAll()
                )
                .sessionManagement(sessionManagement ->
                                           sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}
