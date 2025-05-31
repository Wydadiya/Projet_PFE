package com.pfeProject.Projet_PFE.repository;

import com.pfeProject.Projet_PFE.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByCin(String cin);
    boolean existsByEmail(String email);
    boolean existsByCin(String cin);
}