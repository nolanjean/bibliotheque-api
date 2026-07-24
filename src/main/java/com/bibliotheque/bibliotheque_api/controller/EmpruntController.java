package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.response.EmpruntResponse;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.mapper.EmpruntMapper;
import com.bibliotheque.bibliotheque_api.service.EmpruntService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService){
        this.empruntService = empruntService;
    }

    @GetMapping
    public List<EmpruntResponse> listEmprunt(){
        List<Emprunt> listEmprunt = empruntService.listerTousLesEmprunts();
        return listEmprunt.stream().map(EmpruntMapper::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public EmpruntResponse trouverEmprunt(@PathVariable Long id){
        Emprunt emprunt = empruntService.trouverParId(id);
        return EmpruntMapper.toResponse(emprunt);
    }

    @GetMapping("/membre/{membreId}")
    public List<EmpruntResponse> listEmpruntMembre(@PathVariable Long membreId){
        List<Emprunt> listEmpruntMembre = empruntService.listerEmpruntsDuMembre(membreId);
        return listEmpruntMembre.stream().map(EmpruntMapper::toResponse).collect(Collectors.toList());
    }

    @PostMapping
    public EmpruntResponse emprunterLivre(@RequestParam Long membreId, @RequestParam Long livreId){
        Emprunt emprunt = empruntService.emprunterLivre(membreId, livreId);
        return EmpruntMapper.toResponse(emprunt);
    }

    @PutMapping("/{empruntId}/rendre")
    public EmpruntResponse rendreLivre(@PathVariable Long empruntId){
        Emprunt emprunt = empruntService.rendreLivre(empruntId);
        return EmpruntMapper.toResponse(emprunt);
    }

}
