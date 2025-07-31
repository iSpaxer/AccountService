package com.example.service;

import com.example.entity.GitHubEntity;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.UserRepository;
import com.example.security.oauth2.ExternalOAuth2Handler;
import com.example.security.oauth2.PrincipalOAuth2User;
import com.example.util.PasswordGenerator;
import com.example.util.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class GitHubOAuth2Service implements ExternalOAuth2Handler {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public GitHubOAuth2Service(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public String getRegistrationId() {
        return "github";
    }

    @Override
    public OAuth2User handle(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        Map<String, Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        Long githubId = ((Integer) attributes.get("id")).longValue();
        User user = userRepository.findByGitHub_GitHubId(((Integer) attributes.get("id")).longValue())
                .orElseGet(() -> {
                    String email = (String) attributes.get("email");

                    if (email == null) {
                        email = fetchEmail(oAuth2UserRequest);
                    }

                    User _user = new User();
                    _user.setEmail(email);
                    _user.setGitHub(new GitHubEntity(githubId,
                                                     (String) attributes.get("login"),
                                                     (String) attributes.get("avatar_url"),
                                                     (String) attributes.get("name"),
                                                     (String) attributes.get("location"),
                                                     Instant.parse((String) attributes.get("created_at"))));

                    _user.setStatus(StatusType.ACTIVE);
                    _user.setUsername(
                            attributes.get("login") + "_" + UUID.randomUUID().toString().substring(0, 8));
                    _user.setPassword(
                            passwordEncoder.encode(
                                    PasswordGenerator.generateRandomPassword(12, _user.getGitHub().toString())));

                    userRepository.save(_user);
                    return _user;
                });
        attributes.put("id", user.getId());

        attributes.put("github_id", githubId);
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());

        return new PrincipalOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "id",
                user.getId()
        );
    }

    private String fetchEmail(OAuth2UserRequest oAuth2UserRequest) {
        HttpHeaders headers = new HttpHeaders();

        OAuth2AccessToken token = oAuth2UserRequest.getAccessToken();
        if (token.getTokenType() == OAuth2AccessToken.TokenType.BEARER) {
            headers.setBearerAuth(token.getTokenValue());
            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    request,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> emailObjects = response.getBody();

            for (Map<String, Object> emailEntry : emailObjects) {
                Boolean primary = (Boolean) emailEntry.get("primary");
                if (Boolean.TRUE.equals(primary)) {
                    return (String) emailEntry.get("email");
                }
            }

            throw new IllegalStateException("No primary email found for GitHub user.");
        }

        throw new BadRequestException("Bad token type for OAuth2");
    }


}

