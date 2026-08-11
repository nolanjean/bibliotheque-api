package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.mapper.LivreMapper;
import com.bibliotheque.bibliotheque_api.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
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
        Livre livreCreer = livreService.creerLivre(request);
        return LivreMapper.toResponse(livreCreer);
    }

    @PutMapping("/{id}")
    public LivreResponse mettreAJour(@PathVariable Long id, @RequestBody Livre livreModifie){
        Livre livre = livreService.mettreAJour(id, livreModifie);
        return LivreMapper.toResponse(livre);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        livreService.supprimer(id);
    }
}