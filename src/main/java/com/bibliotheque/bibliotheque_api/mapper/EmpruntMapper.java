package com.bibliotheque.bibliotheque_api.mapper;


import com.bibliotheque.bibliotheque_api.dto.response.EmpruntResponse;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;

public class EmpruntMapper {

    public static EmpruntResponse toResponse(Emprunt emprunt){
        return new EmpruntResponse(
                emprunt.getId(),
                emprunt.getMembre().getNom(),
                emprunt.getLivre().getTitre(),
                emprunt.getDateEmprunt(),
                emprunt.getDateRetourPrevue(),
                emprunt.getStatut()
        );
    }
}
