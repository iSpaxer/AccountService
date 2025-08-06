package com.example.controller;

import com.example.entity.redis.AccountInfo;
import com.example.security.DefaultAuthenticationPrincipal;
import com.example.service.JwtRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api" + "/v${app.version}/")
public class AuthController {

    private final JwtRedisService jwtRedisService;

    @Autowired
    public AuthController(JwtRedisService jwtRedisService) {this.jwtRedisService = jwtRedisService;}

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AccountInfo>> getAccountInfo(
            @AuthenticationPrincipal DefaultAuthenticationPrincipal authenticationPrincipal) {
        return ResponseEntity.ok(jwtRedisService.getActiveAccounts(authenticationPrincipal.getToken()));
    }

}
