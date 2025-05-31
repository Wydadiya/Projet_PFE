package com.pfeProject.Projet_PFE.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank(message = "Le nom ne peut pas être vide")
    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom ne peut pas être vide")
    @Size(max = 100, message = "Le prénom ne peut pas dépasser 100 caractères")
    private String prenom;

    @NotBlank(message = "Le CIN ne peut pas être vide")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9]{6}$", message = "Le CIN doit être composé de 1 ou 2 lettres suivies de 6 chiffres")
    private String cin;

    @NotBlank(message = "Le genre ne peut pas être vide")
    @Pattern(regexp = "HOMME|FEMME", message = "Le genre doit être HOMME ou FEMME")
    private String genre;

    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'adresse e-mail doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial")
    private String motDePasse;
}