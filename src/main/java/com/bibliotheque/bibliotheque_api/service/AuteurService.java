package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.exception.AuteurPossedeLivresException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.AuteurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuteurService {

    private final AuteurRepository auteurRepository;

    public AuteurService(AuteurRepository auteurRepository) {
        this.auteurRepository = auteurRepository;
    }

    public List<Auteur> listerTousLesAuteurs(){
        return auteurRepository.findAll();
    }

    public Auteur trouverParId(Long id){
        return auteurRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Auteur", id));
    }

    public Auteur creerAuteur(Auteur auteur){
        return auteurRepository.save(auteur);
    }

    public Auteur mettreAJour(Long id, Auteur auteurModifie){
        Auteur auteurExistant = trouverParId(id);

        if (auteurModifie.getNom() != null) {
            auteurExistant.setNom(auteurModifie.getNom());
        }

        return auteurRepository.save(auteurExistant);
    }

    public void supprimer(Long id){
        Auteur auteur = trouverParId(id);
        if(!auteur.getLivres().isEmpty()){
            throw new AuteurPossedeLivresException("Cet auteur possède des livres");
        }
        auteurRepository.delete(auteur);
    }
}