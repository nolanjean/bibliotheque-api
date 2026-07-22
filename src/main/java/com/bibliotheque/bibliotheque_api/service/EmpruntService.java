package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmpruntService {

    private final EmpruntRepository empruntRepository;
    private final MembreRepository membreRepository;
    private final LivreRepository livreRepository;

    public EmpruntService(EmpruntRepository empruntRepository,
                          MembreRepository membreRepository,
                          LivreRepository livreRepository) {
        this.empruntRepository = empruntRepository;
        this.membreRepository = membreRepository;
        this.livreRepository = livreRepository;
    }

    public void emprunterLivre(Long membreId, Long livreId){
        Membre membre = membreRepository.findById(membreId).orElseThrow();
        List<Emprunt> empruntsEnCours = empruntRepository.findByMembreIdAndStatut(membreId, StatutEmprunt.EN_COURS);
        if (empruntsEnCours.size() >= 3 ){
            throw new RuntimeException("Le membre a atteint la limite de 3 emprunts");
        }
        Livre livre = livreRepository.findById(livreId).orElseThrow();
        List<Emprunt> empruntLivreEnCours = empruntRepository.findByLivreIdAndStatut(livreId, StatutEmprunt.EN_COURS);
        if (empruntLivreEnCours.size() >= livre.getNombreExemplaires() ){
            throw new RuntimeException("Plus de livre disponible");
        }
        Emprunt emprunt = new Emprunt();
        emprunt.setMembre(membre);
        emprunt.setLivre(livre);
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        empruntRepository.save(emprunt);
    }

    public void rendreLivre(Long empruntId){
        Emprunt emprunt = empruntRepository.findById(empruntId).orElseThrow();
        if (emprunt.getStatut() == StatutEmprunt.RENDU){
            throw new RuntimeException("Ce livre a déjà été rendu");
        }
        emprunt.setStatut(StatutEmprunt.RENDU);
        empruntRepository.save(emprunt);
    }
}