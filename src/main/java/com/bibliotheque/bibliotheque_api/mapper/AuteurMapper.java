package com.bibliotheque.bibliotheque_api.mapper;

import com.bibliotheque.bibliotheque_api.dto.response.AuteurResponse;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Livre;

import java.util.stream.Collectors;

public class AuteurMapper {

    public static AuteurResponse toResponse(Auteur auteur){
        return new AuteurResponse(
                auteur.getId(),
                auteur.getNom(),
                auteur.getLivres().stream().map(Livre::getTitre).collect(Collectors.toList())
        );
    }
}
