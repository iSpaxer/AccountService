package com.example.security.jwt.deserializer;

import com.example.dto.jwt.JwtToken;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.shaded.gson.JsonParseException;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RefreshTokenJweDeserializer implements Function<String, JwtToken> {

    JWEDecrypter jweDecrypter;


    @Override
    public JwtToken apply(String string) {
        try {
            var encryptedJWT = EncryptedJWT.parse(string);
            encryptedJWT.decrypt(this.jweDecrypter);
            var claimsSet = encryptedJWT.getJWTClaimsSet();
            return new JwtToken(
                    claimsSet.getLongClaim("id"),
                    claimsSet.getSubject(),
                    claimsSet.getStringListClaim("authorities"),
                    claimsSet.getIssueTime().toInstant(),
                    claimsSet.getExpirationTime().toInstant());
        } catch (ParseException | JOSEException exception) {
            throw new JsonParseException("Refresh токен не десериализуем");
        }

//        return null;
    }
}
