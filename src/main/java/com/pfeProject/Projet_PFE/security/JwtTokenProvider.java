// JwtTokenProvider.java
package com.pfeProject.Projet_PFE.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwt-expiration-milliseconds}")
    private long jwtExpirationDate;

    private final SecretKey key;

    public JwtTokenProvider(@Value("${app.jwt-secret}") String jwtSecret) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(key)
                .compact();
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("Erreur lors de l'extraction du nom d'utilisateur: {}", e.getMessage());
            throw new RuntimeException("Token JWT invalide", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token); // Utilise parseSignedClaims au lieu de parse
            return true;
        } catch (ExpiredJwtException e) {
            logger.error("JWT token expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token non supporté: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("JWT token malformé: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Signature JWT invalide: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string vide: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur JWT inattendue: {}", e.getMessage());
        }
        return false;
    }

    // Méthode utilitaire pour extraire la date d'expiration
    public Date getExpirationDateFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
        } catch (JwtException e) {
            logger.error("Erreur lors de l'extraction de la date d'expiration: {}", e.getMessage());
            return null;
        }
    }
}