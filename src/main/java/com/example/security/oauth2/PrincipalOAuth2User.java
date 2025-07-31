package com.example.security.oauth2;

import com.example.security.auth.AuthPrincipalWithId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class PrincipalOAuth2User extends DefaultOAuth2User implements AuthPrincipalWithId {

    private final Long id;

    public PrincipalOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes, String nameAttributeKey, Long id) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
    }

    @Override
    public Long getId() {
        return id;
    }
}
