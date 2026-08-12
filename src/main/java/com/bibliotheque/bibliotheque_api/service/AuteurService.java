package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.exception.AuteurPossedeLivresException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.AuteurRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuteurService {

    private static final Logger logger = LoggerFactory.getLogger(AuteurService.class);
    private final AuteurRepository auteurRepository;

    public AuteurService(AuteurRepository auteurRepository) {
        this.auteurRepository = auteurRepository;
    }

    public Page<Auteur> listerTousLesAuteurs(Pageable pageable){
        return auteurRepository.findAll(pageable);
    }

    public Auteur trouverParId(Long id) {
        return auteurRepository.findById(id).orElseThrow(() -> {
            logger.warn("Auteur introuvable, ID {}", id);
            return new RessourceNotFoundException("Auteur", id);
        });
    }

    public Auteur creerAuteur(Auteur auteur){
        logger.info("Tentative de création d'un Auteur {}", auteur);

        Auteur auteurSauvegarder = auteurRepository.save(auteur);
        logger.info("Auteur crée avec succès, ID {}", auteurSauvegarder.getId());

        return auteurSauvegarder;
    }

    public Auteur mettreAJour(Long id, Auteur auteurModifie){
        Auteur auteurExistant = trouverParId(id);

        if (auteurModifie.getNom() != null) {
            auteurExistant.setNom(auteurModifie.getNom());
        }

        Auteur auteurMisAJour = auteurRepository.save(auteurExistant);
        logger.info("Auteur mis a jour, ID {}", id);

        return auteurMisAJour;
    }

    public void supprimer(Long id){
        Auteur auteur = trouverParId(id);
        if(!auteur.getLivres().isEmpty()){
            logger.warn("Cet auteur possède des livres, ID {}", id);
            throw new AuteurPossedeLivresException("Cet auteur possède des livres");
        }

        auteurRepository.delete(auteur);
        logger.info("Auteur supprimé, ID {}",id);
    }
}