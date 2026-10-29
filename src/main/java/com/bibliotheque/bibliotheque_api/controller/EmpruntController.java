package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.response.EmpruntResponse;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.Role;
import com.bibliotheque.bibliotheque_api.exception.ApiError;
import com.bibliotheque.bibliotheque_api.mapper.EmpruntMapper;
import com.bibliotheque.bibliotheque_api.service.EmpruntService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Emprunts", description = "Emprunts de livres : un MEMBRE voit et crée ses propres emprunts, l'ADMIN gère tout")
@ApiResponses({
        @ApiResponse(responseCode = "400", description = "Requête invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "401", description = "Authentification requise ou token invalide",
                content = @Content(schema = @Schema(implementation = ApiError.class))),
        @ApiResponse(responseCode = "403", description = "Droits insuffisants",
                content = @Content(schema = @Schema(implementation = ApiError.class)))
})
@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService){
        this.empruntService = empruntService;
    }

    @Operation(summary = "Lister tous les emprunts (ADMIN)", description = "Liste paginée. Taille de page maximale : 100.")
    @ApiResponse(responseCode = "200", description = "Page d'emprunts")
    @GetMapping
    public Page<EmpruntResponse> listEmprunt(@ParameterObject Pageable pageable){
        Page<Emprunt> listEmprunt = empruntService.listerTousLesEmprunts(pageable);
        return listEmprunt.map(EmpruntMapper::toResponse);
    }

    @Operation(summary = "Détail d'un emprunt (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt trouvé"),
            @ApiResponse(responseCode = "404", description = "Emprunt introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @GetMapping("/{id}")
    public EmpruntResponse trouverEmprunt(@PathVariable Long id){
        Emprunt emprunt = empruntService.trouverParId(id);
        return EmpruntMapper.toResponse(emprunt);
    }

    @Operation(summary = "Emprunts d'un membre donné (ADMIN)", description = "Liste paginée des emprunts du membre indiqué")
    @ApiResponse(responseCode = "200", description = "Page d'emprunts du membre")
    @GetMapping("/membre/{membreId}")
    public Page<EmpruntResponse> listEmpruntMembre(@PathVariable Long membreId, @ParameterObject Pageable pageable){
        Page<Emprunt> listEmpruntMembre = empruntService.listerEmpruntsDuMembre(membreId, pageable);
        return listEmpruntMembre.map(EmpruntMapper::toResponse);
    }

    @Operation(summary = "Mes emprunts", description = "Emprunts du membre connecté (identifié par son token), paginés. Accessible aux ADMIN et aux MEMBRE.")
    @ApiResponse(responseCode = "200", description = "Page de mes emprunts")
    @GetMapping("/mes-emprunts")
    public Page<EmpruntResponse> mesEmprunts(@Parameter(hidden = true) @AuthenticationPrincipal Membre connecte,
                                             @ParameterObject Pageable pageable){
        Page<Emprunt> emprunts = empruntService.listerEmpruntsDuMembre(connecte.getId(), pageable);
        return emprunts.map(EmpruntMapper::toResponse);
    }

    @Operation(summary = "Emprunter un livre",
            description = "Sans membreId, l'emprunt est fait pour le membre connecté. "
                    + "Avec membreId, seul un ADMIN peut emprunter au nom d'un autre membre (un MEMBRE reçoit un 403). "
                    + "Règles : 3 emprunts en cours maximum, et au moins un exemplaire disponible.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprunt créé"),
            @ApiResponse(responseCode = "404", description = "Livre ou membre introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Limite d'emprunts atteinte ou livre indisponible",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping
    public EmpruntResponse emprunterLivre(
            @Parameter(description = "Identifiant du membre pour qui emprunter (ADMIN uniquement)")
            @RequestParam(required = false) Long membreId,
            @Parameter(description = "Identifiant du livre à emprunter")
            @RequestParam Long livreId,
            @Parameter(hidden = true) @AuthenticationPrincipal Membre connecte){
        Long idCible = connecte.getId();

        if (membreId != null && !membreId.equals(connecte.getId())) {
            if (connecte.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Un membre ne peut emprunter que pour lui-même");
            }
            idCible = membreId;
        }

        Emprunt emprunt = empruntService.emprunterLivre(idCible, livreId);
        return EmpruntMapper.toResponse(emprunt);
    }

    @Operation(summary = "Déclarer le retour d'un livre (ADMIN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Retour enregistré"),
            @ApiResponse(responseCode = "404", description = "Emprunt introuvable",
                    content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "409", description = "Emprunt déjà rendu",
                    content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PutMapping("/{empruntId}/rendre")
    public EmpruntResponse rendreLivre(@PathVariable Long empruntId){
        Emprunt emprunt = empruntService.rendreLivre(empruntId);
        return EmpruntMapper.toResponse(emprunt);
    }

}