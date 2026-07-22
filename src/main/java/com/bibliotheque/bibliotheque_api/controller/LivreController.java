package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.service.LivreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/livres")
public class LivreController {

    private final LivreService livreService;

    public LivreController(LivreService livreService) {
        this.livreService = livreService;
    }

    @GetMapping
    public List<Livre> listerTousLesLivres(){
        return livreService.listerTousLesLivres();
    }

    @GetMapping("/{id}")
    public Livre trouverLivre(@PathVariable Long id){
        return livreService.trouverParId(id);
    }
    @PostMapping
    public Livre creerLivre(@RequestBody Livre livre){
        return livreService.creerLivre(livre);
    }

    @PutMapping("/{id}")
    public Livre mettreAJour(@PathVariable Long id, @RequestBody Livre livreModifie){
        return livreService.mettreAJour(id, livreModifie);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        livreService.supprimer(id);
    }
}