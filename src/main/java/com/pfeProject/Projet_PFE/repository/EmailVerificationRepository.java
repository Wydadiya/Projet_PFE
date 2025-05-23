package com.pfeProject.Projet_PFE.repository;

import com.pfeProject.Projet_PFE.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Integer> {
    EmailVerification findByToken(String token);
}
