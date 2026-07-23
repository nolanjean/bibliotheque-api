package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.service.MembreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService){
        this.membreService = membreService;
    }

    @GetMapping
    public List<Membre> listMembres(){
        return membreService.listerTousLesMembres();
    }

    @GetMapping("/{id}")
    public Membre trouverMembre(@PathVariable Long id){
        return membreService.trouverParId(id);
    }

    @PostMapping
    public Membre creerMembre(@RequestBody Membre membre){
        return membreService.creerMembre(membre);
    }

    @PutMapping("/{id}")
    public Membre mettreAJour(@PathVariable Long id, @RequestBody Membre membreModifie){
        return membreService.mettreAJour(id, membreModifie);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        membreService.supprimer(id);
    }
}
