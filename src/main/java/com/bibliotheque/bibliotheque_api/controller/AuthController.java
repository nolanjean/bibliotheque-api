package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LoginRequest;
import com.bibliotheque.bibliotheque_api.dto.request.RegisterRequest;
import com.bibliotheque.bibliotheque_api.dto.response.MembreResponse;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.exception.IdentifiantsInvalidesException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.mapper.MembreMapper;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import com.bibliotheque.bibliotheque_api.security.jwt.JwtService;
import com.bibliotheque.bibliotheque_api.service.MembreService;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final MembreService membreService;
    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(MembreService membreService, MembreRepository membreRepository,
                          PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.membreService = membreService;
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public MembreResponse register(@Valid @RequestBody RegisterRequest request) {
        Membre membre = membreService.creerMembre(request);
        return MembreMapper.toResponse(membre);
    }

    @PostMapping("/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest request) {
        Membre membre = membreRepository.findByEmail(request.email())
                .orElseThrow(() -> new IdentifiantsInvalidesException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.motDePasse(), membre.getMotDePasse())) {
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        String token = jwtService.genererToken(membre.getEmail());
        return Map.of("token", token);
    }
}
