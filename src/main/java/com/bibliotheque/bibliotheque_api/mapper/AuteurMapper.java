package com.bibliotheque.bibliotheque_api.mapper;

import com.bibliotheque.bibliotheque_api.dto.request.AuteurCreateRequest;
import com.bibliotheque.bibliotheque_api.dto.request.AuteurUpdateRequest;
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

    public static Auteur toEntity(AuteurCreateRequest request){
        Auteur auteur = new Auteur();
        auteur.setNom(request.nom());
        return auteur;
    }

    public static Auteur toEntity(AuteurUpdateRequest request){
        Auteur auteur = new Auteur();
        auteur.setNom(request.nom());
        return auteur;
    }
}
