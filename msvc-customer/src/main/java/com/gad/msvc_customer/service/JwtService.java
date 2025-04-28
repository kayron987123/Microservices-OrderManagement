package com.gad.msvc_customer.service;

import com.gad.msvc_customer.exception.JwtDecodingException;
import com.gad.msvc_customer.exception.KeyFactoryCreationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Service
public class JwtService {
    @Value("${rsa.key.public-key}")
    private String propertyPublicKey;

    public UUID getUserUuidFromJwt(String token) {
        String tokenClean = token.substring(7);

        try {
            PublicKey publicKey = loadPublicKey();
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(tokenClean)
                    .getBody();

            String uuidString = claims.get("uuid_customer", String.class);
            return UUID.fromString(uuidString);
        } catch (RuntimeException e) {
            throw new JwtDecodingException("JWT could not be decoded", e);
        }
    }

    private PublicKey loadPublicKey() {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(propertyPublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = createKeyFactory();
            return generatePublicKey(keyFactory, spec);
        } catch (RuntimeException e) {
            throw new JwtDecodingException("Error loading public key", e);
        }
    }

    private KeyFactory createKeyFactory() {
        try {
            return KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new KeyFactoryCreationException("Error creating Key factory for RSA", e);
        }
    }

    private PublicKey generatePublicKey(KeyFactory keyFactory, X509EncodedKeySpec spec) {
        try {
            return keyFactory.generatePublic(spec);
        } catch (InvalidKeySpecException e) {
            throw new JwtDecodingException("Invalid public key", e);
        }
    }
}
