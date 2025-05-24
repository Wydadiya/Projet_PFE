package com.pfeProject.Projet_PFE.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCandidature etatCandidature = EtatCandidature.EN_ATTENTE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateSoumission = LocalDateTime.now();

    private LocalDateTime dateExpiration;
    private LocalDateTime dateConfirmation;

    // Encadrant (peut être null au début)
    @ManyToOne
    @JoinColumn(name = "encadrant_id")
    private Utilisateur encadrant;

    //  Enum pour les états
    public enum EtatCandidature {
        EN_ATTENTE,
        ACCEPTEE_ATTENTE_CONFIRMATION,
        ACCEPTEE_CONFIRMEE,
        REJETEE,
        ANNULEE
    }
}
