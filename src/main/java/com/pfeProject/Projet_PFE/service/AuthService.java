package com.pfeProject.Projet_PFE.service;

import com.pfeProject.Projet_PFE.dto.RegisterRequest;
import com.pfeProject.Projet_PFE.entity.*;
import com.pfeProject.Projet_PFE.repository.*;
import com.pfeProject.Projet_PFE.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private RoleUtilisateurRepository roleUtilisateurRepository;
    @Autowired
    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern CIN_PATTERN = Pattern.compile("^[A-Z]{2}\\d{6}$");
    private static final int MIN_PASSWORD_LENGTH = 8;

    public void registerCandidat(RegisterRequest request) {
        logger.info("Tentative d'inscription pour email: {}", request.getEmail());

        // Validation de l'email
        if (!EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            logger.error("Email invalide: {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email invalide");
        }

        // Validation du CIN
        if (request.getCin() != null && !CIN_PATTERN.matcher(request.getCin()).matches()) {
            logger.error("Format du CIN invalide: {}", request.getCin());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Format du CIN invalide (doit être 2 lettres suivies de 6 chiffres)");
        }

        // Vérification de l'unicité de l'email
        if (utilisateurRepository.findByEmail(request.getEmail()) != null) {
            logger.error("Email déjà utilisé: {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }

        // Vérification de l'unicité du CIN
        if (request.getCin() != null && utilisateurRepository.findByCin(request.getCin()) != null) {
            logger.error("CIN déjà utilisé: {}", request.getCin());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CIN déjà utilisé");
        }

        // Validation du mot de passe
        if (request.getMotDePasse().length() < MIN_PASSWORD_LENGTH) {
            logger.error("Mot de passe trop court: {}", request.getMotDePasse());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mot de passe trop court (minimum 8 caractères)");
        }

        // Validation du genre
        try {
            Utilisateur.Genre.valueOf(request.getGenre());
        } catch (IllegalArgumentException e) {
            logger.error("Genre invalide: {}", request.getGenre());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Genre invalide (doit être HOMME ou FEMME)");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setCin(request.getCin());
        utilisateur.setGenre(Utilisateur.Genre.valueOf(request.getGenre()));
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateurRepository.save(utilisateur);

        RoleUtilisateur role = new RoleUtilisateur();
        role.setUtilisateur(utilisateur);
        role.setRole(RoleUtilisateur.Role.Candidat);
        roleUtilisateurRepository.save(role);

        EmailVerification verification = new EmailVerification();
        verification.setUtilisateur(utilisateur);
        verification.setToken(UUID.randomUUID().toString());
        verification.setDateExpiration(LocalDateTime.now().plusDays(1));
        emailVerificationRepository.save(verification);

        logger.info("Inscription réussie pour utilisateur: {}", utilisateur.getEmail());
    }

    public String loginCandidatStagiaire(String cin, String motDePasse) {
        logger.info("Tentative de connexion candidat/stagiaire avec CIN: {}", cin);

        if (cin == null || cin.isEmpty()) {
            logger.error("CIN requis");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CIN requis");
        }

        Utilisateur utilisateur = utilisateurRepository.findByCin(cin);
        if (utilisateur == null || !passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            logger.error("Identifiants incorrects pour CIN: {}", cin);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects");
        }

        logger.debug("Rôles trouvés pour utilisateur {}: {}", utilisateur.getEmail(), utilisateur.getRoles());
        RoleUtilisateur role = utilisateur.getRoles().stream()
                .filter(r -> r.getRole() == RoleUtilisateur.Role.Candidat || r.getRole() == RoleUtilisateur.Role.Stagiaire)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Rôle non trouvé (Candidat ou Stagiaire requis) pour CIN: {}", cin);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Rôle non trouvé (Candidat ou Stagiaire requis)");
                });

        String token = jwtUtil.generateToken(cin, role.getRole().name());
        logger.info("Connexion réussie pour CIN: {}", cin);
        return token;
    }

    public String loginEncadrant(String email, String motDePasse) {
        logger.info("Tentative de connexion encadrant avec email: {}", email);

        if (email == null || email.isEmpty()) {
            logger.error("Email requis");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email requis");
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur == null || !passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            logger.error("Identifiants incorrects pour email: {}", email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects");
        }

        logger.debug("Rôles trouvés pour utilisateur {}: {}", utilisateur.getEmail(), utilisateur.getRoles());
        RoleUtilisateur role = utilisateur.getRoles().stream()
                .filter(r -> r.getRole() == RoleUtilisateur.Role.Encadrant)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Rôle non trouvé (Encadrant requis) pour email: {}", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Rôle non trouvé (Encadrant requis)");
                });

        String token = jwtUtil.generateToken(email, role.getRole().name());
        logger.info("Connexion réussie pour email: {}", email);
        return token;
    }

    public String loginAdmin(String email, String motDePasse) {
        logger.info("Tentative de connexion admin avec email: {}", email);

        if (email == null || email.isEmpty()) {
            logger.error("Email requis");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email requis");
        }

        Utilisateur utilisateur = utilisateurRepository.findByEmail(email);
        if (utilisateur == null || !passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            logger.error("Identifiants incorrects pour email: {}", email);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects");
        }

        logger.debug("Rôles trouvés pour utilisateur {}: {}", utilisateur.getEmail(), utilisateur.getRoles());
        RoleUtilisateur role = utilisateur.getRoles().stream()
                .filter(r -> r.getRole() == RoleUtilisateur.Role.Administrateur)
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Rôle non trouvé (Administrateur requis) pour email: {}", email);
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Rôle non trouvé (Administrateur requis)");
                });

        String token = jwtUtil.generateToken(email, role.getRole().name());
        logger.info("Connexion réussie pour email: {}", email);
        return token;
    }
}