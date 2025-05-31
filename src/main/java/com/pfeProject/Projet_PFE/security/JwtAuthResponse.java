package com.pfeProject.Projet_PFE.security;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class JwtAuthResponse {
    private String accessToken;
    private String tokenType;
    private String email;
    private Set<String> roles;

    public JwtAuthResponse(String accessToken, String tokenType, String email, Set<String> roles) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.email = email;
        this.roles = roles;
    }
}