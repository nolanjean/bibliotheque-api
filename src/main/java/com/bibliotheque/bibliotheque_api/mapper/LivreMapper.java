package com.bibliotheque.bibliotheque_api.mapper;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.response.LivreResponse;
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



}
