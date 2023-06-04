package com.epam.esm.jwt;

import com.epam.esm.model.Role;
import com.epam.esm.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtProvider {
    private static final String CLAIM_ID = "id";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_ROLE = "role";
    private static final String ISSUER = "self";
    private static final int TOKEN_LIFE_TIME = 60;
    private final Key secretKey;

    public JwtProvider(@Value("${jwt.secret.key}") String jwtSecretKey) {
        this.secretKey = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Date issued = Date.from(now);
        Date expiration = Date.from(now.plus(TOKEN_LIFE_TIME, ChronoUnit.MINUTES));
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuer(ISSUER)
                .setIssuedAt(issued)
                .setExpiration(expiration)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .claim(CLAIM_ID, Long.valueOf(user.getId()))
                .claim(CLAIM_USERNAME, user.getUsername())
                .claim(CLAIM_ROLE, user.getRole())
                .compact();
    }

    public JwtAuthentication getAuthentication(String token) {
        if (token == null) {
            return null;
        }
        Claims claims = getClaims(token);
        JwtAuthentication jwtAuthentication = new JwtAuthentication();
        jwtAuthentication.setEmail(claims.getSubject());
        jwtAuthentication.setUsername(claims.get(CLAIM_USERNAME, String.class));
        jwtAuthentication.setRole(Role.valueOf(claims.get(CLAIM_ROLE, String.class)));
        jwtAuthentication.setId(claims.get(CLAIM_ID, Long.class));
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
