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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
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
                .orElseThrow(() -> {
                    logger.warn("tentative de connexion avec un email incconu : {}", request.email());
                    return new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
                });

        if (!passwordEncoder.matches(request.motDePasse(), membre.getMotDePasse())) {
            logger.warn("tentative de connexion avec mot de passe incorrect pour cet email : {}", request.email());
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        String token = jwtService.genererToken(membre.getEmail());
        logger.info("connexion réussie pour l'email : {}", membre.getEmail());
        return Map.of("token", token);
    }
}
