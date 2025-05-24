package com.pfeProject.Projet_PFE.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Stage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //  Stage lié à une candidature acceptée
    @OneToOne
    @JoinColumn(name = "candidature_id", nullable = false)
    private Candidature candidature;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String sujetStage;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatStage etatStage = EtatStage.EN_COURS;

    private boolean archive = false;

    //  Enum pour l’état du stage
    public enum EtatStage {
        EN_COURS,
        TERMINE,
        ANNULE
    }
}
