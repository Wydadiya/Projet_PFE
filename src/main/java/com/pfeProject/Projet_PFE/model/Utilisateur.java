package com.pfeProject.Projet_PFE.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    @Column(unique = true, length = 20)
    private String cin;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    @Column(length = 20)
    private String telephone;

    @Column(length = 255)
    private String photoProfil;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    private LocalDateTime dateModification;

    @OneToMany(mappedBy = "utilisateur", cascade = CascadeType.ALL)
    private List<RoleUtilisateur> roles;

    public enum Genre {
        HOMME,
        FEMME
    }
}
