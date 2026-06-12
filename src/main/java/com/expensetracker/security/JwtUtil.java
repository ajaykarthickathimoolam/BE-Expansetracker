package com.expensetracker.security;

import com.expensetracker.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String CLAIM_USER_ID = "uid";
    private static final String CLAIM_USERNAME = "un";

    private final SecretKey key;
    private final long expirationMs;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {
        this.key = Keys.hmacShaKeyFor(sha256(secret));
        this.expirationMs = expirationMs;
    }

    private static byte[] sha256(String secret) {
        try {
            return MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            var msg = """
                    SHA-256 algorithm not available — JVM security \
                    configuration may be broken.
                    """;
            throw new IllegalStateException(msg.strip(), e);
        }
    }

    public String generateToken(User user) {
        var now = new Date();
        var expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(user.getEmail())
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_USERNAME, user.getUsername())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return parseClaims(token).get(CLAIM_USERNAME, String.class);
    }

    public String extractUserId(String token) {
        return parseClaims(token).get(CLAIM_USER_ID, String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            var claims = parseClaims(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Used with pattern matching in filter — distinguishes valid claims vs descriptive errors. */
    public Object validateOrDescribeFailure(String token) {
        try {
            if (token == null || token.isBlank()) {
                return "Missing bearer token.";
            }
            var claims = parseClaims(token);
            if (claims.getExpiration().before(new Date())) {
                return "Expired JWT.";
            }
            return claims;
        } catch (Exception ex) {
            var template = """
                    Could not validate JWT (%s): %s
                    """.formatted(ex.getClass().getSimpleName(), ex.getMessage());
            return template.strip();
        }
    }
}
