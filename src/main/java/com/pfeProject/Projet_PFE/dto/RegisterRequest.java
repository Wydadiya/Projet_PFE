package com.pfeProject.Projet_PFE.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String cin;
    private String genre;
    private String email;
    private String motDePasse;
}