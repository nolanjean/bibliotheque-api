package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.IsbnDejaExistantException;
import com.bibliotheque.bibliotheque_api.exception.LivreNonRenduException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.AuteurRepository;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LivreService {

    private final LivreRepository livreRepository;
    private final EmpruntRepository empruntRepository;
    private final AuteurRepository auteurRepository;

    public LivreService(LivreRepository livreRepository, EmpruntRepository empruntRepository, AuteurRepository auteurRepository) {
        this.livreRepository = livreRepository;
        this.empruntRepository = empruntRepository;
        this.auteurRepository = auteurRepository;
    }

    public List<Livre> listerTousLesLivres(){
        return livreRepository.findAll();
    }

    public Livre trouverParId(Long id){
        return livreRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Livre", id));
    }

    public Livre creerLivre(LivreCreateRequest request){
        if (livreRepository.findByIsbn(request.isbn()).isPresent()){
            throw new IsbnDejaExistantException("Isbn déjà existant");
        }
        Auteur auteur = auteurRepository.findById(request.auteurId()).orElseThrow(() -> new RessourceNotFoundException("Auteur", request.auteurId()));
        Livre livre = new Livre();
        livre.setTitre(request.titre());
        livre.setIsbn(request.isbn());
        livre.setNombreExemplaires(request.nombreExemplaires());
        livre.setAuteur(auteur);
        return livreRepository.save(livre);
    }

    public Livre mettreAJour(Long id, Livre livreModifie){
        Livre livreExistant = trouverParId(id);

        if (livreModifie.getTitre() != null) {
            livreExistant.setTitre(livreModifie.getTitre());
        }
        if (livreModifie.getIsbn() != null) {
            livreExistant.setIsbn(livreModifie.getIsbn());
        }
        if (livreModifie.getNombreExemplaires() != 0) {
            livreExistant.setNombreExemplaires(livreModifie.getNombreExemplaires());
        }
        if (livreModifie.getAuteur() != null) {
            livreExistant.setAuteur(livreModifie.getAuteur());
        }

        return livreRepository.save(livreExistant);
    }

    public void supprimer(Long id){
        Livre livre = trouverParId(id);

        List<Emprunt> listLivreEmprunter = empruntRepository.findByLivreIdAndStatut(id, StatutEmprunt.EN_COURS);
        if (!listLivreEmprunter.isEmpty()){
            throw new LivreNonRenduException("Livre encore non rendu");
        }
        livreRepository.delete(livre);
    }
}