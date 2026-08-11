package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.response.EmpruntResponse;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.mapper.EmpruntMapper;
import com.bibliotheque.bibliotheque_api.service.EmpruntService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    private final EmpruntService empruntService;

    public EmpruntController(EmpruntService empruntService){
        this.empruntService = empruntService;
    }

    @GetMapping
    public Page<EmpruntResponse> listEmprunt(Pageable pageable){
        Page<Emprunt> listEmprunt = empruntService.listerTousLesEmprunts(pageable);
        return listEmprunt.map(EmpruntMapper::toResponse);
    }

    @GetMapping("/{id}")
    public EmpruntResponse trouverEmprunt(@PathVariable Long id){
        Emprunt emprunt = empruntService.trouverParId(id);
        return EmpruntMapper.toResponse(emprunt);
    }

    @GetMapping("/membre/{membreId}")
    public Page<EmpruntResponse> listEmpruntMembre(@PathVariable Long membreId, Pageable pageable){
        Page<Emprunt> listEmpruntMembre = empruntService.listerEmpruntsDuMembre(membreId, pageable);
        return listEmpruntMembre.map(EmpruntMapper::toResponse);
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
