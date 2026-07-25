package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.response.MembreResponse;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.mapper.MembreMapper;
import com.bibliotheque.bibliotheque_api.service.MembreService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membres")
public class MembreController {

    private final MembreService membreService;

    public MembreController(MembreService membreService){
        this.membreService = membreService;
    }

    @GetMapping
    public List<MembreResponse> listMembres(){
        List<Membre> membres = membreService.listerTousLesMembres();
        return membres.stream()
                .map(MembreMapper::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public MembreResponse trouverMembre(@PathVariable Long id){
        Membre membre = membreService.trouverParId(id);
        return MembreMapper.toResponse(membre);
    }

    @PutMapping("/{id}")
    public MembreResponse mettreAJour(@PathVariable Long id, @RequestBody Membre membreModifie){
        Membre membre = membreService.mettreAJour(id, membreModifie);
        return MembreMapper.toResponse(membre);
    }

    @DeleteMapping("/{id}")
    public void supprimer(@PathVariable Long id){
        membreService.supprimer(id);
    }
}
