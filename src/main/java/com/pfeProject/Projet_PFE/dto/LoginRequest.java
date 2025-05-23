package com.pfeProject.Projet_PFE.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String cin;
    private String email;
    private String motDePasse;
}