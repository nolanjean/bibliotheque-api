package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.request.LivreUpdateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.mapper.LivreMapper;
import com.bibliotheque.bibliotheque_api.service.AuteurService;
import com.bibliotheque.bibliotheque_api.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;
    private final AuteurService auteurService;

    public LivreController(LivreService livreService, AuteurService auteurService) {
        this.livreService = livreService;
        this.auteurService = auteurService;
    }

    @GetMapping
    public Page<LivreResponse> listerTousLesLivres(Pageable pageable){
        Page<Livre> pageLivres = livreService.listerTousLesLivres(pageable);
        return pageLivres.map(LivreMapper::toResponse);
    }

    @GetMapping("/{id}")
    public LivreResponse trouverLivre(@PathVariable Long id){
        Livre livre = livreService.trouverParId(id);
        return LivreMapper.toResponse(livre);
    }
    @PostMapping
    public LivreResponse creerLivre(@Valid @RequestBody LivreCreateRequest request){
        Auteur auteur = auteurService.trouverParId(request.auteurId());
        Livre livre = LivreMapper.toEntity(request,auteur);
        Livre livreCreer = livreService.creerLivre(livre);
        return LivreMapper.toResponse(livreCreer);
    }

    @PutMapping("/{id}")
    public LivreResponse mettreAJour(@PathVariable Long id, @Valid @RequestBody LivreUpdateRequest request){
        Auteur auteur = null;
        if (request.auteurId() != null) {
            auteur = auteurService.trouverParId(request.auteurId());
        }
        Livre livre = livreService.mettreAJour(id, request, auteur);
        return LivreMapper.toResponse(livre);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        livreService.supprimer(id);
    }
}