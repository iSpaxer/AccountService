package com.example.service;

import com.example.entity.GitHubUser;
import com.example.rep.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GitHubService {

    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private final String clientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private final String clientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect}")
    private final String redirectUri;

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public String buildGitHubRedirectUrl() {
        return "https://github.com/login/oauth/authorize?client_id=" + clientId + "&redirect_uri=" + redirectUri;
    }

    public String handleGitHubCallback(String code) {
        String accessToken = fetchAccessToken(code);
        GitHubUser githubUser = fetchGitHubUser(accessToken);
        return code;
        //        User user = userRepository.findByGithubId(githubUser.getId())
        //                .orElseGet(() -> userRepository.save(new User(githubUser.getId(), githubUser.getEmail())));
        //
        //        return jwtUtil.generateToken(user.getId());
    }

    private String fetchAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("redirect_uri", redirectUri);

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://github.com/login/oauth/access_token", request, Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    private GitHubUser fetchGitHubUser(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<?> request = new HttpEntity<>(headers);

        ResponseEntity<GitHubUser> response = restTemplate.exchange(
                "https://api.github.com/user", HttpMethod.GET, request, GitHubUser.class
        );
        return response.getBody();
    }
}

