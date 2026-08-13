package com.bibliotheque.bibliotheque_api.mapper;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Livre;

public class LivreMapper {

    public static LivreResponse toResponse(Livre livre){
        return new LivreResponse(
                livre.getId(),
                livre.getTitre(),
                livre.getIsbn(),
                livre.getNombreExemplaires(),
                livre.getAuteur().getNom()
        );
    }

    public static Livre toEntity(LivreCreateRequest request, Auteur auteur){
        Livre livre = new Livre();
        livre.setTitre(request.titre());
        livre.setIsbn(request.isbn());
        livre.setNombreExemplaires(request.nombreExemplaires());
        livre.setAuteur(auteur);
        return livre;
    }

}
