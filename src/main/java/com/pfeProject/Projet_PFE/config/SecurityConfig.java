package com.pfeProject.Projet_PFE.config;

import com.pfeProject.Projet_PFE.util.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Désactiver CSRF (non nécessaire pour les API REST stateless)
                .csrf(AbstractHttpConfigurer::disable)

                // Configuration de la gestion de session (stateless pour JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configuration des autorisations HTTP
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics d'authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // Endpoints spécifiques par rôle
                        .requestMatchers("/api/admin/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers("/api/encadrant/**").hasRole("ENCADRANT")
                        .requestMatchers("/api/stagiaire/**").hasAnyRole("STAGIAIRE", "CANDIDAT")

                        // Endpoints publics optionnels (documentation, santé, etc.)
                        .requestMatchers("/", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()

                        // Toutes les autres requêtes nécessitent une authentification
                        .anyRequest().authenticated())

                // Ajouter le filtre JWT avant le filtre d'authentification standard
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // Construire la configuration
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}