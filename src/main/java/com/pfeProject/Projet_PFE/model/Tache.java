package com.pfeProject.Projet_PFE.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter



@NoArgsConstructor
@AllArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Plusieurs taches pour un seul stage
    @ManyToOne
    @JoinColumn(name = "stage_id", nullable = false)
    private Stage stage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priorite priorite = Priorite.MOYENNE;

    @Column(nullable = false)
    private LocalDate echeance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private EtatTache etatTache = EtatTache.ASSIGNEE;

    @Column(length = 255)
    private String livrable; // chemin ou URL (peut rester null)

    // ==== ENUMS internes ====

    public enum Priorite {
        BASSE, MOYENNE, HAUTE
    }

    public enum EtatTache {
        ASSIGNEE, EN_COURS, SOUMISE, VALIDEE
    }
}
