package com.example.security.oauth2;

import com.example.security.auth.AuthPrincipalAbstractIdentifier;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class PrincipalOAuth2User extends DefaultOAuth2User implements AuthPrincipalAbstractIdentifier {

    private final Long id;
    private final String jti;

    public PrincipalOAuth2User(
            Collection<? extends GrantedAuthority> authorities,
            Map<String, Object> attributes, String nameAttributeKey, Long id, String jti) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
        this.jti = jti;
    }

}
