package com.example.security.filter;

import com.example.dto.jwt.JwtResponse;
import com.example.security.converter.CustomAuthenticationConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.function.Function;


/**
 * Фильтр для аутификации пользователя по {email: ..; password: ..;}
 * Фильтр не пропускает запрос в сервлет, отдает ответ сам.
 */
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtLoginFilter extends OncePerRequestFilter {

    DaoAuthenticationProvider daoAuthenticationProvider;

    Function<Authentication, JwtResponse> authenticationJwtResponseMapper;

    ObjectMapper objectMapper = new ObjectMapper();

    // todo потом нужно удалить)
    private RequestMatcher requestMatcher = new AntPathRequestMatcher("/api/jwt/login", HttpMethod.POST.name());

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (this.requestMatcher.matches(request)) {
            var authentication = new CustomAuthenticationConverter().convert(request);
            Authentication authenticate;
            authenticate = daoAuthenticationProvider.authenticate(authentication);
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getWriter(), authenticationJwtResponseMapper.apply(authenticate));
            return;
        }
        filterChain.doFilter(request, response);
    }
}
