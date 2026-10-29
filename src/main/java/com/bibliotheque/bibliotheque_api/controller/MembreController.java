package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.MembreUpdateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.MembreResponse;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.exception.ApiError;
import com.bibliotheque.bibliotheque_api.mapper.MembreMapper;
import com.bibliotheque.bibliotheque_api.service.MembreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Membres", description = "Gestion des membres, réservée aux ADMIN")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Requête invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise ou token invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService){
        this.membreService = membreService;
    }

    @Operation(summary = "Lister les membres (ADMIN)",
            description = "Liste paginée. Paramètre q : recherche dans le nom et l'email. "
                    + "Tri possible sur id, nom, email (ex : sort=nom,asc). Taille de page maximale : 100.")
    @ApiResponse(responseCode = "200", description = "Page de membres")
    @GetMapping
    public Page<MembreResponse> listerTousLesMembres(
            @Parameter(description = "Texte recherché dans le nom ou l'email")
            @RequestParam(required = false) String q,
            @ParameterObject Pageable pageable) {
        return membreService.rechercher(q, pageable).map(MembreMapper::toResponse);
    }

    @Operation(summary = "Détail d'un membre (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membre trouvé"),
            @ApiResponse(responseCode = "404", description = "Membre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public MembreResponse trouverMembre(@PathVariable Long id){
        Membre membre = membreService.trouverParId(id);
        return MembreMapper.toResponse(membre);
    }

    @Operation(summary = "Modifier un membre (ADMIN)",
            description = "Mise à jour partielle du nom, de l'email et du mot de passe. Le rôle n'est pas modifiable.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membre modifié"),
            @ApiResponse(responseCode = "404", description = "Membre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Email déjà utilisé",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public MembreResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody MembreUpdateRequest request){
        Membre membre = membreService.mettreAJour(id, request);
        return MembreMapper.toResponse(membre);
    }

    @Operation(summary = "Supprimer un membre (ADMIN)",
            description = "Bloqué si le membre a des emprunts en cours. Son historique d'emprunts est supprimé avec lui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Membre supprimé"),
            @ApiResponse(responseCode = "404", description = "Membre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Le membre a des emprunts en cours",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        membreService.supprimer(id);
    }
}