package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.request.LivreUpdateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.exception.ApiError;
import com.bibliotheque.bibliotheque_api.mapper.LivreMapper;
import com.bibliotheque.bibliotheque_api.service.AuteurService;
import com.bibliotheque.bibliotheque_api.service.LivreService;
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

@Tag(name = "Livres", description = "Catalogue des livres : consultation publique, gestion réservée aux ADMIN")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Requête invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise ou token invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;
    private final AuteurService auteurService;

    public LivreController(LivreService livreService, AuteurService auteurService) {
        this.livreService = livreService;
        this.auteurService = auteurService;
    }

    @Operation(summary = "Lister les livres",
            description = "Liste paginée (public). Paramètre q : recherche dans le titre et l'ISBN. "
                    + "Tri possible sur id, titre, isbn (ex : sort=titre,asc). Taille de page maximale : 100.")
    @ApiResponse(responseCode = "200", description = "Page de livres")
    @GetMapping
    public Page<LivreResponse> listerTousLesLivres(
            @Parameter(description = "Texte recherché dans le titre ou l'ISBN")
            @RequestParam(required = false) String q,
            @Parameter(description = "Filtre sur l'identifiant de l'auteur")
            @RequestParam(required = false) Long auteurId,
            @ParameterObject Pageable pageable) {
        Page<Livre> pageLivres = livreService.rechercher(q, auteurId, pageable);
        return pageLivres.map(LivreMapper::toResponse);
    }

    @Operation(summary = "Détail d'un livre", description = "Accès public")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre trouvé"),
            @ApiResponse(responseCode = "404", description = "Livre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public LivreResponse trouverLivre(@PathVariable Long id){
        Livre livre = livreService.trouverParId(id);
        return LivreMapper.toResponse(livre);
    }

    @Operation(summary = "Créer un livre (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre créé"),
            @ApiResponse(responseCode = "404", description = "Auteur introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "ISBN déjà existant",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public LivreResponse creerLivre(@Valid @RequestBody LivreCreateRequest request){
        Auteur auteur = auteurService.trouverParId(request.auteurId());
        Livre livre = LivreMapper.toEntity(request,auteur);
        Livre livreCreer = livreService.creerLivre(livre);
        return LivreMapper.toResponse(livreCreer);
    }

    @Operation(summary = "Modifier un livre (ADMIN)",
            description = "Mise à jour partielle : seuls les champs fournis sont modifiés")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre modifié"),
            @ApiResponse(responseCode = "404", description = "Livre ou auteur introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "ISBN déjà existant",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{id}")
    public LivreResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody LivreUpdateRequest request){
        Auteur auteur = null;
        if (request.auteurId() != null) {
            auteur = auteurService.trouverParId(request.auteurId());
        }
        Livre livre = livreService.mettreAJour(id, request, auteur);
        return LivreMapper.toResponse(livre);
    }

    @Operation(summary = "Supprimer un livre (ADMIN)",
            description = "Bloqué si des exemplaires sont encore empruntés. L'historique des emprunts du livre est supprimé avec lui.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livre supprimé"),
            @ApiResponse(responseCode = "404", description = "Livre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Des exemplaires sont encore empruntés",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        livreService.supprimer(id);
    }
}