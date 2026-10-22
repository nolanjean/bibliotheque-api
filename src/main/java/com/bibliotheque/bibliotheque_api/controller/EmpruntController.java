package com.bibliotheque.bibliotheque_api.controller;

import com.bibliotheque.bibliotheque_api.dto.response.EmpruntResponse;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.mapper.EmpruntMapper;
import com.bibliotheque.bibliotheque_api.service.EmpruntService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.Role;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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

    @GetMapping("/mes-emprunts")
    public Page<EmpruntResponse> mesEmprunts(@AuthenticationPrincipal Membre connecte, Pageable pageable){
        Page<Emprunt> emprunts = empruntService.listerEmpruntsDuMembre(connecte.getId(), pageable);
        return emprunts.map(EmpruntMapper::toResponse);
    }

    @PostMapping
    public EmpruntResponse emprunterLivre(@RequestParam(required = false) Long membreId,
                                          @RequestParam Long livreId,
                                          @AuthenticationPrincipal Membre connecte){
        Long idCible = connecte.getId();

        if (membreId != null && !membreId.equals(connecte.getId())) {
            if (connecte.getRole() != Role.ADMIN) {
                throw new AccessDeniedException("Un membre ne peut emprunter que pour lui-même");
            }
            idCible = membreId;
        }

        Emprunt emprunt = empruntService.emprunterLivre(idCible, livreId);
        return EmpruntMapper.toResponse(emprunt);
    }

    @PutMapping("/{empruntId}/rendre")
    public EmpruntResponse rendreLivre(@PathVariable Long empruntId){
        Emprunt emprunt = empruntService.rendreLivre(empruntId);
        return EmpruntMapper.toResponse(emprunt);
    }

}
