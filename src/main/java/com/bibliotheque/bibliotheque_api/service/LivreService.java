package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
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
        return livreRepository.findById(id).orElseThrow(()-> new RuntimeException("Livre introuvable"));
    }

    public Livre creerLivre(Livre livre){
        Long auteurId = livre.getAuteur().getId();
        auteurRepository.findById(auteurId).orElseThrow(()-> new RuntimeException("Auteur introuvable"));
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
            throw new RuntimeException("Livre encore non rendu");
        }
        livreRepository.delete(livre);
    }
}