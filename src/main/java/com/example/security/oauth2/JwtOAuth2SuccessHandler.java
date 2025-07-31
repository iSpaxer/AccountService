package com.example.security.oauth2;

import com.example.security.jwt.factory.AuthenticationJwtResponseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class JwtOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationJwtResponseMapper authenticationJwtResponseMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtOAuth2SuccessHandler(AuthenticationJwtResponseMapper authenticationJwtResponseMapper) {
        this.authenticationJwtResponseMapper = authenticationJwtResponseMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), authenticationJwtResponseMapper.apply(authentication));

    }
}
