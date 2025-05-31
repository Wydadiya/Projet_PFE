
package com.pfeProject.Projet_PFE.service;

import com.pfeProject.Projet_PFE.dto.LoginCinDto;
import com.pfeProject.Projet_PFE.dto.LoginEmailDto;
import com.pfeProject.Projet_PFE.dto.RegisterRequest;
import com.pfeProject.Projet_PFE.entity.RoleUtilisateur;
import com.pfeProject.Projet_PFE.entity.Utilisateur;
import com.pfeProject.Projet_PFE.repository.RoleUtilisateurRepository;
import com.pfeProject.Projet_PFE.repository.UtilisateurRepository;
import com.pfeProject.Projet_PFE.security.JwtAuthResponse;
import com.pfeProject.Projet_PFE.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UtilisateurRepository utilisateurRepository;
    private final RoleUtilisateurRepository roleUtilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public void registerCandidat(@Valid RegisterRequest request) {
        logger.info("Tentative d'inscription pour email: {}", request.getEmail());

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            logger.error("Email déjà utilisé: {}", request.getEmail());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email déjà utilisé");
        }

        if (utilisateurRepository.existsByCin(request.getCin())) {
            logger.error("CIN déjà utilisé: {}", request.getCin());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CIN déjà utilisé");
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setCin(request.getCin());
        utilisateur.setGenre(request.getGenre() != null ? Utilisateur.Genre.valueOf(request.getGenre()) : null);
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setCreatedAt(LocalDateTime.now());
        utilisateur.setUpdatedAt(LocalDateTime.now());

        RoleUtilisateur role = roleUtilisateurRepository.findByRole(RoleUtilisateur.Role.CANDIDAT)
                .orElseGet(() -> {
                    RoleUtilisateur newRole = new RoleUtilisateur();
                    newRole.setRole(RoleUtilisateur.Role.CANDIDAT);
                    return roleUtilisateurRepository.save(newRole);
                });
        utilisateur.setRole(role);

        utilisateurRepository.save(utilisateur);

        logger.info("Inscription réussie pour utilisateur: {}", utilisateur.getEmail());
    }

    @Override
    public JwtAuthResponse loginByEmail(@Valid LoginEmailDto request) {
        logger.info("Tentative de connexion avec email: {}", request.getEmail());

        try {
            Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Utilisateur non trouvé"));

            if (!utilisateur.hasRole(RoleUtilisateur.Role.ENCADRANT) && !utilisateur.hasRole(RoleUtilisateur.Role.ADMINISTRATEUR)) {
                logger.error("Connexion par email non autorisée pour l'utilisateur: {}", request.getEmail());
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Connexion par email réservée aux encadrants et administrateurs");
            }

            // Manual password check for debugging
            if (!passwordEncoder.matches(request.getPassword(), utilisateur.getMotDePasse())) {
                logger.error("Mot de passe incorrect pour email: {}", request.getEmail());
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Mot de passe incorrect");
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jwtTokenProvider.generateToken(authentication);
            Set<String> roles = Collections.singleton(utilisateur.getRole().getRole().name());

            logger.info("Connexion réussie pour email: {}", request.getEmail());
            return new JwtAuthResponse(token, "Bearer", request.getEmail(), roles);

        } catch (BadCredentialsException e) {
            logger.error("Identifiants incorrects pour email: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects", e);
        } catch (Exception e) {
            logger.error("Échec de connexion pour email: {}", request.getEmail(), e);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erreur d'authentification", e);
        }
    }

    @Override
    public JwtAuthResponse loginByCin(@Valid LoginCinDto request) {
        logger.info("Tentative de connexion avec CIN: {}", request.getCin());

        Utilisateur utilisateur = utilisateurRepository.findByCin(request.getCin())
                .orElseThrow(() -> {
                    logger.error("Utilisateur non trouvé pour CIN: {}", request.getCin());
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects");
                });

        if (!utilisateur.hasRole(RoleUtilisateur.Role.CANDIDAT) && !utilisateur.hasRole(RoleUtilisateur.Role.STAGIAIRE)) {
            logger.error("Connexion par CIN non autorisée pour l'utilisateur: {}", utilisateur.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Connexion par CIN réservée aux candidats et stagiaires");
        }

        if (!passwordEncoder.matches(request.getPassword(), utilisateur.getMotDePasse())) {
            logger.error("Mot de passe incorrect pour CIN: {}", request.getCin());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants incorrects");
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                utilisateur.getEmail(),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().getRole().name()))
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        Set<String> roles = Collections.singleton(utilisateur.getRole().getRole().name());

        logger.info("Connexion réussie pour CIN: {}", request.getCin());
        return new JwtAuthResponse(token, "Bearer", utilisateur.getEmail(), roles);
    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }
}