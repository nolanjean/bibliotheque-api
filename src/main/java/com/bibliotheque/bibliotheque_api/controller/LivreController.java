package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.mapper.LivreMapper;
import com.bibliotheque.bibliotheque_api.service.LivreService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public List<LivreResponse> listerTousLesLivres(){
        List<Livre> listLivres = livreService.listerTousLesLivres();
        return listLivres.stream()
                .map(LivreMapper::toResponse)
                .collect(Collectors.toList());
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