package com.restaurant.management.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * <h2>JwtService</h2>
 * <p>
 * This core component handles the lifecycle of security tokens for the
 * Restaurant Management System. It ensures that user identities and roles
 * are cryptographically signed and propagated throughout the microservice
 * fleet.
 * </p>
 * 
 * @author Shivam Srivastav
 * @version 1.0
 */
@Service
public class JwtService {

    @Value("${jwt.secret:rms_secure_access_key_12345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:3600000}")
    private Long expiration;

    public String generateToken(String username, Map<String, Object> claims) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .subject(username)
                .claims(claims)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
