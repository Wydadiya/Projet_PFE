package com.pfeProject.Projet_PFE.service;

import com.pfeProject.Projet_PFE.dto.LoginCinDto;
import com.pfeProject.Projet_PFE.dto.LoginEmailDto;
import com.pfeProject.Projet_PFE.dto.RegisterRequest;
import com.pfeProject.Projet_PFE.security.JwtAuthResponse;

import jakarta.validation.Valid;

public interface AuthService {
    void registerCandidat(@Valid RegisterRequest request);
    JwtAuthResponse loginByEmail(@Valid LoginEmailDto request);
    JwtAuthResponse loginByCin(@Valid LoginCinDto request);
    String hashPassword(String password);
}