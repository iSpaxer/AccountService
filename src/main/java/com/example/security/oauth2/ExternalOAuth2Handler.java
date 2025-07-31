package com.example.security.oauth2;

import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface ExternalOAuth2Handler {

    String getRegistrationId();

    OAuth2User handle(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User);

}
