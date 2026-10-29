package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.AuteurCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.request.AuteurUpdateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.AuteurResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.exception.ApiError;
import com.bibliotheque.bibliotheque_api.mapper.AuteurMapper;
import com.bibliotheque.bibliotheque_api.service.AuteurService;
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

@Tag(name = "Auteurs", description = "Auteurs des livres : consultation publique, gestion réservée aux ADMIN")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Requête invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise ou token invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
@RestController
@RequestMapping("/api/auteurs")
public class AuteurController {

    private final AuteurService auteurService;

    public AuteurController(AuteurService auteurService){
        this.auteurService = auteurService;
    }

    @Operation(summary = "Lister les auteurs",
            description = "Liste paginée (public), avec les titres de leurs livres. Paramètre q : recherche dans le nom. "
                    + "Tri possible sur id et nom (ex : sort=nom,asc). Taille de page maximale : 100.")
    @ApiResponse(responseCode = "200", description = "Page d'auteurs")
    @GetMapping
    public Page<AuteurResponse> listerTousLesAuteurs(
            @Parameter(description = "Texte recherché dans le nom de l'auteur")
            @RequestParam(required = false) String q,
            @ParameterObject Pageable pageable) {
        Page<Auteur> pageAuteurs = auteurService.rechercher(q, pageable);
        return pageAuteurs.map(AuteurMapper::toResponse);
    }

    @Operation(summary = "Détail d'un auteur", description = "Accès public")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auteur trouvé"),
            @ApiResponse(responseCode = "404", description = "Auteur introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public AuteurResponse trouverAuteur(@PathVariable Long id){
        Auteur auteur = auteurService.trouverParId(id);
        return AuteurMapper.toResponse(auteur);
    }

    @Operation(summary = "Créer un auteur (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Auteur créé")
    @PostMapping
    public AuteurResponse creerAuteur(@Valid @RequestBody AuteurCreateRequest request){
        Auteur auteur = AuteurMapper.toEntity(request);
        Auteur auteurCreer = auteurService.creerAuteur(auteur);
        return AuteurMapper.toResponse(auteurCreer);
    }

    @Operation(summary = "Modifier un auteur (ADMIN)",
            description = "Mise à jour partielle : seuls les champs fournis sont modifiés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auteur modifié"),
            @ApiResponse(responseCode = "404", description = "Auteur introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public AuteurResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody AuteurUpdateRequest request){
        Auteur auteurModifie = AuteurMapper.toEntity(request);
        Auteur auteur = auteurService.mettreAJour(id, auteurModifie);
        return AuteurMapper.toResponse(auteur);
    }

    @Operation(summary = "Supprimer un auteur (ADMIN)",
            description = "Bloqué si l'auteur a encore des livres associés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auteur supprimé"),
            @ApiResponse(responseCode = "404", description = "Auteur introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "L'auteur a encore des livres",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        auteurService.supprimer(id);
    }
}