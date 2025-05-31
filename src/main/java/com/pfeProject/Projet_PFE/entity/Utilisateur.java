
package com.pfeProject.Projet_PFE.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "cin", unique = true, length = 8)
    @Pattern(regexp = "^[A-Z]{1,2}[0-9]{6}$", message = "Le CIN doit être composé de 1 ou 2 lettres suivies de 6 chiffres.")
    private String cin;

    @Enumerated(EnumType.STRING)
    @Column(name = "genre", length = 10)
    private Genre genre;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "L'adresse e-mail doit être valide.")
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @Column(name = "telephone", length = 15)
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$", message = "Le numéro de téléphone doit être un numéro marocain valide.")
    private String telephone;

    @Column(name = "photo_profil")
    private String photoProfil;

    @CreationTimestamp
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "date_modification")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private RoleUtilisateur role;

    public enum Genre {
        HOMME, FEMME
    }

    public boolean hasRole(RoleUtilisateur.Role role) {
        return this.role != null && this.role.getRole() == role;
    }

    public boolean isAdmin() {
        return hasRole(RoleUtilisateur.Role.ADMINISTRATEUR);
    }

    public String getFullName() {
        return prenom + " " + nom;
    }

    public Set<RoleUtilisateur> getRoles() {
        return role != null ? new HashSet<>(Collections.singletonList(role)) : new HashSet<>();
    }

    public void setRoles(Set<RoleUtilisateur> roles) {
        this.role = roles != null && !roles.isEmpty() ? roles.iterator().next() : null;
    }
}