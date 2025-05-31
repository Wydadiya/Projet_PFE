package com.pfeProject.Projet_PFE.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginCinDto {
    @NotBlank(message = "Le CIN ne peut pas être vide")
    @Pattern(regexp = "^[A-Z]{1,2}[0-9]{6}$", message = "Le CIN doit être composé de 1 ou 2 lettres suivies de 6 chiffres")
    private String cin;

    @NotBlank(message = "Le mot de passe ne peut pas être vide")
    private String password;
}