package com.pfeProject.Projet_PFE.controller;

import com.pfeProject.Projet_PFE.dto.LoginRequest;
import com.pfeProject.Projet_PFE.dto.RegisterRequest;
import com.pfeProject.Projet_PFE.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.registerCandidat(request);
        return ResponseEntity.ok("Inscription réussie. Vérifiez votre email.");
    }

    @PostMapping("/login/candidat-stagiaire")
    public ResponseEntity<String> loginCandidatStagiaire(@RequestBody LoginRequest request) {
        String token = authService.loginCandidatStagiaire(request.getCin(), request.getMotDePasse());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/encadrant")
    public ResponseEntity<String> loginEncadrant(@RequestBody LoginRequest request) {
        String token = authService.loginEncadrant(request.getEmail(), request.getMotDePasse());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/admin")
    public ResponseEntity<String> loginAdmin(@RequestBody LoginRequest request) {
        String token = authService.loginAdmin(request.getEmail(), request.getMotDePasse());
        return ResponseEntity.ok(token);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getReason(), ex.getStatusCode());
    }
}
