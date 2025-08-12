package com.example.controller;

import com.example.entity.redis.AccountInfo;
import com.example.security.DefaultAuthenticationPrincipal;
import com.example.service.JwtRedisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api" + "/v${app.version}/")
@Tag(name = "account-controller")
public class AccountController {

    private final JwtRedisService jwtRedisService;

    @Autowired
    public AccountController(JwtRedisService jwtRedisService) {this.jwtRedisService = jwtRedisService;}

    @GetMapping("/devices")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<AccountInfo>> getAccountInfo(
            @AuthenticationPrincipal DefaultAuthenticationPrincipal authenticationPrincipal) {
        return ResponseEntity.ok(jwtRedisService.getActiveAccounts(authenticationPrincipal.getToken()));
    }

    @PostMapping("/logout/others")
    @ResponseStatus(HttpStatus.OK)
    @Deprecated //todo объединить с API из фильтра
    public ResponseEntity logoutOthers(
            @AuthenticationPrincipal DefaultAuthenticationPrincipal authenticationPrincipal) {
        jwtRedisService.logoutOthers(authenticationPrincipal.getToken());
        return ResponseEntity.status(HttpStatus.OK).build();
    }


}
