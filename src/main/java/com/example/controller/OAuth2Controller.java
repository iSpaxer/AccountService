package com.example.controller;


import com.example.service.GitHubService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequestMapping("/auth/github")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final GitHubService gitHubService;

    @GetMapping("/start")
    public void redirectToGitHub(HttpServletResponse response) throws IOException {
        String url = gitHubService.buildGitHubRedirectUrl();
        response.sendRedirect(url);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> githubCallback(@RequestParam String code) {
        String jwt = gitHubService.handleGitHubCallback(code);
        return ResponseEntity.ok(Collections.singletonMap("token", jwt));
    }
}
