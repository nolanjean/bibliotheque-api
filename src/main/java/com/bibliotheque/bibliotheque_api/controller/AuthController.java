package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LoginRequest;
import com.bibliotheque.bibliotheque_api.dto.request.RegisterRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LoginResponse;
import com.bibliotheque.bibliotheque_api.dto.response.MembreResponse;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.exception.ApiError;
import com.bibliotheque.bibliotheque_api.exception.IdentifiantsInvalidesException;
import com.bibliotheque.bibliotheque_api.mapper.MembreMapper;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import com.bibliotheque.bibliotheque_api.security.jwt.JwtService;
import com.bibliotheque.bibliotheque_api.service.MembreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentification", description = "Inscription, connexion (JWT) et profil du membre connecté")
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

    @Operation(summary = "Créer un compte",
            description = "Accès public. Le compte créé a toujours le rôle MEMBRE, le rôle ne peut pas être choisi à l'inscription.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Compte créé"),
            @ApiResponse(responseCode = "400", description = "Données invalides (détail par champ dans « champs »)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"erreur\":\"Données invalides\",\"champs\":{\"email\":\"Email invalide\"}}"))),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = "{\"status\":409,\"erreur\":\"Cet email est déjà utilisé\"}")))
    })
    @PostMapping("/register")
    public MembreResponse register(@Valid @RequestBody RegisterRequest request) {
        Membre membre = membreService.creerMembre(request);
        return MembreMapper.toResponse(membre);
    }

    @Operation(summary = "Se connecter",
            description = "Accès public. Renvoie un token JWT à envoyer ensuite dans l'en-tête `Authorization: Bearer {token}`.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "400", description = "Données invalides",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = "{\"status\":400,\"erreur\":\"Données invalides\",\"champs\":{\"email\":\"Email invalide\"}}"))),
            @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"erreur\":\"Email ou mot de passe incorrect\"}")))
    })
    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        Membre membre = membreRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    logger.warn("tentative de connexion avec un email inconnu : {}", request.email());
                    return new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
                });

        if (!passwordEncoder.matches(request.motDePasse(), membre.getMotDePasse())) {
            logger.warn("tentative de connexion avec mot de passe incorrect pour cet email : {}", request.email());
            throw new IdentifiantsInvalidesException("Email ou mot de passe incorrect");
        }

        String token = jwtService.genererToken(membre.getEmail());
        logger.info("connexion réussie pour l'email : {}", membre.getEmail());
        return new LoginResponse(token, membre.getRole().name());
    }

    @Operation(summary = "Mon profil",
            description = "Renvoie le membre identifié par le token. Accessible aux ADMIN et aux MEMBRE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Profil du membre connecté"),
            @ApiResponse(responseCode = "401", description = "Authentification requise ou token invalide",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = "{\"status\":401,\"erreur\":\"Authentification requise ou token invalide\"}")))
    })
    @GetMapping("/me")
    public MembreResponse me(@Parameter(hidden = true) @AuthenticationPrincipal Membre connecte) {
        return MembreMapper.toResponse(connecte);
    }
}