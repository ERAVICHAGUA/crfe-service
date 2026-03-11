package com.engineai.crfe.infrastructure.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Service
public class JwtService {

    @Value("${security.jwt.secret}")
    private String secret;

    public SecretKey key() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT key", e);
        }
    }
}