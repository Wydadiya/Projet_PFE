package com.pfeProject.Projet_PFE.repository;

import com.pfeProject.Projet_PFE.entity.RoleUtilisateur;
import com.pfeProject.Projet_PFE.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoleUtilisateurRepository extends JpaRepository<RoleUtilisateur, Long> {
    // Dans RoleUtilisateurRepository
    Optional<RoleUtilisateur> findByUtilisateurAndRole(Utilisateur utilisateur, RoleUtilisateur.Role role);
    List<RoleUtilisateur> findByUtilisateur(Utilisateur utilisateur);
}