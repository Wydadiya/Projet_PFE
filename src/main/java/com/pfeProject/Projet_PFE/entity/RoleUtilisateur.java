package com.pfeProject.Projet_PFE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role_utilisateur")
public class RoleUtilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role_utilisateur")
    private Long idRoleUtilisateur;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le rôle ne peut pas être nul.")
    @Column(name = "role", nullable = false, length = 20, unique = true)
    private Role role;

    public enum Role {
        CANDIDAT, STAGIAIRE, ENCADRANT, ADMINISTRATEUR
    }

    @Override
    public String toString() {
        return role != null ? role.name() : "ROLE_UNDEFINED";
    }
}