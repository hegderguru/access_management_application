package com.karur.access_management_application.security.authentication.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtTokenProvider {

    public JwtTokenProvider(@Value("${authentication.jwt.secret-key}") String secretKey, @Value("${authentication.jwt.expiration-ms}") Long expiresIn) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.expiresIn = expiresIn;
    }

    private final SecretKey secretKey;
    private final long expiresIn;

    public String generateToken(String subject, Map<String, Object> claims) {
        Instant instant = Instant.now();
        Instant expireInstant = instant.plusMillis(expiresIn);
        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(Date.from(instant))
                .expiration(Date.from(expireInstant))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public static String extractToken(String token) {
        String BEARER = "Bearer ";
        return token.substring(BEARER.length());
    }

}
