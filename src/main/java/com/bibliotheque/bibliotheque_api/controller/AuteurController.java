package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.service.AuteurService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auteurs")
public class AuteurController {

    private final AuteurService auteurService;

    public AuteurController(AuteurService auteurService){
        this.auteurService = auteurService;
    }

    @GetMapping
    public List<Auteur> listAuteur(){
        return auteurService.listerTousLesAuteurs();
    }

    @GetMapping("/{id}")
    public Auteur trouverAuteur(@PathVariable Long id){
        return auteurService.trouverParId(id);
    }

    @PostMapping
    public Auteur creerAuteur(@RequestBody Auteur auteur){
        return auteurService.creerAuteur(auteur);
    }

    @PutMapping("/{id}")
    public Auteur mettreAJour(@PathVariable Long id, @RequestBody Auteur auteurModifie){
        return auteurService.mettreAJour(id, auteurModifie);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        auteurService.supprimer(id);
    }
}
