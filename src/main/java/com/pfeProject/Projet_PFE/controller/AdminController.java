package com.pfeProject.Projet_PFE.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<String> adminEndpoint() {
        return ResponseEntity.ok("Bonjour Administrateur - Accès autorisé");
    }

    @GetMapping("/encadrant")
    public ResponseEntity<String> encadrantEndpoint() {
        return ResponseEntity.ok("Bonjour Encadrant - Accès autorisé");
    }

    @GetMapping("/stagiaire")
    public ResponseEntity<String> stagiaireEndpoint() {
        return ResponseEntity.ok("Bonjour Stagiaire - Accès autorisé");
    }

    @GetMapping("/candidat")
    public ResponseEntity<String> candidatEndpoint() {
        return ResponseEntity.ok("Bonjour Candidat - Accès autorisé");
    }
}