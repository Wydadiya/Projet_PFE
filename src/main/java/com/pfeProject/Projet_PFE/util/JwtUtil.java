package com.pfeProject.Projet_PFE.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Nouvelle façon de générer une clé sécurisée pour HS512
    private final SecretKey key = Jwts.SIG.HS512.key().build();

    // Getter pour la durée d'expiration (utile pour les tests ou la configuration)
    // Configuration externalisée avec valeur par défaut (10 heures)
    @Getter
    @Value("${jwt.expiration:36000000}")
    private long expirationDuration;

    public String generateToken(String username, String role) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationDuration))
                .signWith(key)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUsernameFromToken(String token) {
        try {
            return getClaimsFromToken(token).getSubject();
        } catch (Exception e) {
            logger.warn("Failed to extract username from token: {}", e.getMessage());
            return null;
        }
    }

    public String getRoleFromToken(String token) {
        try {
            return (String) getClaimsFromToken(token).get("role");
        } catch (Exception e) {
            logger.warn("Failed to extract role from token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            logger.debug("Token validation failed: token is null or empty");
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);

            // Vérifier aussi que le token n'est pas expiré
            return !isTokenExpired(token);

        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("Token validation failed due to illegal argument: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error during token validation: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        if (token == null || token.trim().isEmpty()) {
            return true;
        }

        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            return expiration != null && expiration.before(new Date());
        } catch (Exception e) {
            logger.warn("Failed to check token expiration: {}", e.getMessage());
            return true;
        }
    }

    // Méthode utile pour obtenir le temps restant avant expiration (en millisecondes)
    public long getTokenRemainingTime(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                long remaining = expiration.getTime() - System.currentTimeMillis();
                return Math.max(0, remaining);
            }
        } catch (Exception e) {
            logger.warn("Failed to get token remaining time: {}", e.getMessage());
        }
        return 0;
    }

    // Méthode pour rafraîchir un token (générer un nouveau token avec les mêmes informations)
    public String refreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            String role = (String) claims.get("role");

            if (username != null) {
                return generateToken(username, role);
            }
        } catch (Exception e) {
            logger.warn("Failed to refresh token: {}", e.getMessage());
        }
        return null;
    }

}