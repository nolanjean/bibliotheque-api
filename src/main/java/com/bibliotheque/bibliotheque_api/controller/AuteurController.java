package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.AuteurCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.AuteurResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.mapper.AuteurMapper;
import com.bibliotheque.bibliotheque_api.service.AuteurService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auteurs")
public class AuteurController {

    private final AuteurService auteurService;

    public AuteurController(AuteurService auteurService){
        this.auteurService = auteurService;
    }

    @GetMapping
    public Page<AuteurResponse> listAuteur(Pageable pageable){
        Page<Auteur> auteur = auteurService.listerTousLesAuteurs(pageable);
        return auteur.map(AuteurMapper::toResponse);
    }

    @GetMapping("/{id}")
    public AuteurResponse trouverAuteur(@PathVariable Long id){
        Auteur auteur = auteurService.trouverParId(id);
        return AuteurMapper.toResponse(auteur);
    }

    @PostMapping
    public AuteurResponse creerAuteur(@Valid @RequestBody AuteurCreateRequest request){
        Auteur auteur = AuteurMapper.toEntity(request);
        Auteur auteurCreer = auteurService.creerAuteur(auteur);
        return AuteurMapper.toResponse(auteurCreer);
    }

    @PutMapping("/{id}")
    public AuteurResponse mettreAJour(@PathVariable Long id, @RequestBody Auteur auteurModifie){
        Auteur auteur = auteurService.mettreAJour(id, auteurModifie);
        return AuteurMapper.toResponse(auteur);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        auteurService.supprimer(id);
    }
}
