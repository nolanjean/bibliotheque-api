package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.service.EmpruntService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService){
        this.empruntService = empruntService;
    }

    @GetMapping
    public List<Emprunt> listEmprunt(){
        return empruntService.listerTousLesEmprunts();
    }

    @GetMapping("/{id}")
    public Emprunt trouverEmprunt(@PathVariable Long id){
        return empruntService.trouverParId(id);
    }

    @GetMapping("/membre/{membreId}")
    public List<Emprunt> listEmpruntMembre(@PathVariable Long membreId){
        return empruntService.listerEmpruntsDuMembre(membreId);
    }

    @PostMapping
    public void emprunterLivre(@RequestParam Long membreId, @RequestParam Long livreId){
        empruntService.emprunterLivre(membreId, livreId);
    }

    @PutMapping("/{empruntId}/rendre")
    public void rendreLivre(@PathVariable Long empruntId){
        empruntService.rendreLivre(empruntId);
    }

}
