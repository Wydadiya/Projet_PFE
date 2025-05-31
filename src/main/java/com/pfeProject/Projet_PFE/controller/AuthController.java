package com.pfeProject.Projet_PFE.controller;

import com.pfeProject.Projet_PFE.dto.LoginCinDto;
import com.pfeProject.Projet_PFE.dto.LoginEmailDto;
import com.pfeProject.Projet_PFE.dto.RegisterRequest;
import com.pfeProject.Projet_PFE.security.JwtAuthResponse;
import com.pfeProject.Projet_PFE.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            authService.registerCandidat(registerRequest);
            return new ResponseEntity<>("Utilisateur inscrit avec succès.", HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription: {}", e.getMessage());
            return new ResponseEntity<>("Erreur lors de l'inscription: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login/email")
    public ResponseEntity<JwtAuthResponse> loginByEmail(@Valid @RequestBody LoginEmailDto loginDto) {
        try {
            JwtAuthResponse response = authService.loginByEmail(loginDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion par email: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/login/cin")
    public ResponseEntity<JwtAuthResponse> loginByCin(@Valid @RequestBody LoginCinDto loginDto) {
        try {
            JwtAuthResponse response = authService.loginByCin(loginDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erreur lors de la connexion par CIN: {}", e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/test/hash")
    public String hashPassword(@RequestParam String password) {
        return authService.hashPassword(password);
    }
}