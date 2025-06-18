package com.example.PostService.security.jwt.deserializer;

import com.example.PostService.dto.jwt.JwtToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.function.Function;


@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccessTokenJwsDeserializer implements Function<String, JwtToken> {

    JWSVerifier jwsVerifier;
    JWSAlgorithm jwsAlgorithm;

    public AccessTokenJwsDeserializer(JWSVerifier jwsVerifier) {
        this.jwsVerifier = jwsVerifier;
        this.jwsAlgorithm = JWSAlgorithm.HS256;
    }

    @Override
    public JwtToken apply(String stringToken) {
        try {
            var signedJWT = SignedJWT.parse(stringToken);
            if (signedJWT.verify(jwsVerifier)) {
                var jwtClaimsSet = signedJWT.getJWTClaimsSet();
                return new JwtToken(
                        jwtClaimsSet.getSubject(),
                        jwtClaimsSet.getStringListClaim("authorities"),
                        jwtClaimsSet.getIssueTime().toInstant(),
                        jwtClaimsSet.getExpirationTime().toInstant()
                );
            }
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


}
