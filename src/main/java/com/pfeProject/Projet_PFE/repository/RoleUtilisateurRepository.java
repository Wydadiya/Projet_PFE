package com.pfeProject.Projet_PFE.repository;

import com.pfeProject.Projet_PFE.entity.RoleUtilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleUtilisateurRepository extends JpaRepository<RoleUtilisateur, Long> {
    Optional<RoleUtilisateur> findByRole(RoleUtilisateur.Role role);
}