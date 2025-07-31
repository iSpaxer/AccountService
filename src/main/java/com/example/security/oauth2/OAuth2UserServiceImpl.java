package com.example.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    private final Map<String, ExternalOAuth2Handler> handlers;

    @Autowired
    public OAuth2UserServiceImpl(List<ExternalOAuth2Handler> handlerList) {
        this.handlers = handlerList.stream()
                .collect(Collectors.toMap(ExternalOAuth2Handler::getRegistrationId, Function.identity()));
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        return handlers.get(provider).handle(userRequest, oAuth2User);
    }
}
