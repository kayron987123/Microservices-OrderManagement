package com.gad.msvc_customer.service;

import com.gad.msvc_customer.exception.JwtDecodingException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.KeyPair;
import java.util.Base64;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {
    @InjectMocks
    private JwtService jwtService;

    private KeyPair keyPair;

    @BeforeEach
    void setUp() {
        keyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        String publicKeyBase64 = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        ReflectionTestUtils.setField(jwtService, "propertyPublicKey", publicKeyBase64);
    }

    @Test
    @DisplayName("Should return UUID when token is valid")
    void givenValidToken_whenGetUserUuidFromJwt_thenReturnsUuid() {
        UUID expectedUuid = UUID.randomUUID();
        String token = generateTestToken(expectedUuid);

        UUID result = jwtService.getUserUuidFromJwt("Bearer " + token);

        assertEquals(expectedUuid, result);
    }

    @Test
    @DisplayName("Should throw JwtDecodingException when token dont have prefix Bearer")
    void givenInvalidTokenFormat_whenGetUserUuidFromJwt_thenThrowsException() {
        assertThrows(JwtDecodingException.class, () -> jwtService.getUserUuidFromJwt("invalid token"));
    }

    @Test
    @DisplayName("Should throw JwtDecodingException when key for sign is invalid")
    void givenTokenWithInvalidSignature_whenGetUserUuidFromJwt_thenThrowsException() {
        KeyPair otherKeyPair = Keys.keyPairFor(SignatureAlgorithm.RS256);
        String invalidToken = Jwts.builder()
                .claim("uuid_customer", UUID.randomUUID().toString())
                .signWith(otherKeyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        assertThrows(JwtDecodingException.class, () ->
                jwtService.getUserUuidFromJwt("Bearer " + invalidToken)
        );
    }

    @Test
    @DisplayName("Should throw JwtDecodingException when token doesnt have claim uuid_customer")
    void givenTokenWithoutUuidClaim_whenGetUserUuidFromJwt_thenThrowsException() {
        String tokenWithoutUuid = Jwts.builder()
                .claim("other_claim", "value")
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();

        assertThrows(JwtDecodingException.class, () ->
                jwtService.getUserUuidFromJwt("Bearer " + tokenWithoutUuid)
        );
    }

    @Test
    @DisplayName("Should throw JwtDecodingException when public key is malformed")
    void givenMalformedPublicKey_whenGetUserUuidFromJwt_thenThrowsException() {
        ReflectionTestUtils.setField(jwtService, "propertyPublicKey", "invalid_public_key");

        assertThrows(JwtDecodingException.class, this::invokeGetUserUuidFromJwtWithBearerToken);
    }

    private void invokeGetUserUuidFromJwtWithBearerToken() {
        jwtService.getUserUuidFromJwt("Bearer " + generateTestToken(UUID.randomUUID()));
    }

    private String generateTestToken(UUID uuid) {
        return Jwts.builder()
                .claim("uuid_customer", uuid.toString())
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }
}