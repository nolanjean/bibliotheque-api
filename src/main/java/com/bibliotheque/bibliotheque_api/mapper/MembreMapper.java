package com.bibliotheque.bibliotheque_api.mapper;

import com.bibliotheque.bibliotheque_api.dto.response.MembreResponse;
import com.bibliotheque.bibliotheque_api.entity.Membre;

public class MembreMapper {

    public static MembreResponse toResponse(Membre membre){
        return new MembreResponse(
                membre.getId(),
                membre.getNom(),
                membre.getEmail(),
                membre.getRole()
        );
    }
}
