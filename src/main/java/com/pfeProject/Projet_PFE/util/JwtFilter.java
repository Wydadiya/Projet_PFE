package com.pfeProject.Projet_PFE.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        logger.info("Processing request for path: {}", request.getRequestURI());
        try {
            String token = extractTokenFromRequest(request);
            logger.info("Extracted token: {}", token != null ? "present" : "null");

            if (token != null && jwtUtil.validateToken(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                String role = jwtUtil.getRoleFromToken(token);
                logger.info("Token valid, username: {}, role: {}", username, role);

                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    setAuthenticationInContext(request, username, role);
                    logger.debug("Successfully authenticated user: {} with role: {}", username, role);
                }
            } else {
                logger.info("No valid token found, proceeding without authentication");
            }
        } catch (Exception e) {
            logger.error("Error in JwtFilter: {}", e.getMessage(), e);
        }
        chain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de la requête HTTP
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Configure l'authentification dans le contexte de sécurité
     */
    private void setAuthenticationInContext(HttpServletRequest request, String username, String role) {
        // Construire l'autorité avec le préfixe ROLE_
        String authority = role != null ? ROLE_PREFIX + role : ROLE_PREFIX + "USER";

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username,
                null,
                Collections.singletonList(new SimpleGrantedAuthority(authority))
        );

        // Ajouter les détails de la requête pour plus de sécurité
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    /**
     * Détermine si ce filtre doit s'exécuter pour cette requête
     * Optionnel: permet d'exclure certaines routes
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean shouldNotFilter = path.startsWith("/api/auth/") ||
                path.startsWith("/api/public/") ||
                path.equals("/") ||
                path.startsWith("/swagger-ui") ||
                path.startsWith("/v3/api-docs");
        logger.info("shouldNotFilter for path {}: {}", path, shouldNotFilter);
        return shouldNotFilter;
    }
}