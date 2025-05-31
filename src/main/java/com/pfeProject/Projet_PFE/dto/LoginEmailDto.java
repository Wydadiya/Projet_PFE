package com.pfeProject.Projet_PFE.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginEmailDto {
    @NotBlank(message = "L'email ne peut pas être vide")
    @Email(message = "L'adresse e-mail doit être valide")
    private String email;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;
}