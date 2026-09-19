package com.smartnutrition.security;

import com.smartnutrition.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token.expiry:900000}") long accessTokenExpiry,
            @Value("${jwt.refresh-token.expiry:604800000}") long refreshTokenExpiry) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public String generateAccessToken(Long userId, String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiry);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiry);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
