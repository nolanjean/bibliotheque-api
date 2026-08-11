package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.EmpruntDejaRenduException;
import com.bibliotheque.bibliotheque_api.exception.LimiteEmpruntException;
import com.bibliotheque.bibliotheque_api.exception.LivreIndisponibleException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Page<Emprunt> listerTousLesEmprunts(Pageable pageable) {
        return empruntRepository.findAll(pageable);
    }

    public Emprunt trouverParId(Long id) {
        return empruntRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Emprunt", id));
    }

    public Page<Emprunt> listerEmpruntsDuMembre(Long membreId, Pageable pageable) {
        return empruntRepository.findByMembreId(membreId, pageable);
    }

    public Emprunt emprunterLivre(Long membreId, Long livreId){
        Membre membre = membreRepository.findById(membreId).orElseThrow(() -> new RessourceNotFoundException("Membre", membreId));
        List<Emprunt> empruntsEnCours = empruntRepository.findByMembreIdAndStatut(membreId, StatutEmprunt.EN_COURS);
        if (empruntsEnCours.size() >= 3 ){
            throw new LimiteEmpruntException("Le membre a atteint la limite de 3 emprunts");
        }
        Livre livre = livreRepository.findById(livreId).orElseThrow(() -> new RessourceNotFoundException("Livre", livreId));
        List<Emprunt> empruntLivreEnCours = empruntRepository.findByLivreIdAndStatut(livreId, StatutEmprunt.EN_COURS);
        if (empruntLivreEnCours.size() >= livre.getNombreExemplaires() ){
            throw new LivreIndisponibleException("Plus de livre disponible");
        }
        Emprunt emprunt = new Emprunt();
        emprunt.setMembre(membre);
        emprunt.setLivre(livre);
        emprunt.setDateEmprunt(LocalDate.now());
        emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
        emprunt.setStatut(StatutEmprunt.EN_COURS);
        return empruntRepository.save(emprunt);
    }

    public Emprunt rendreLivre(Long empruntId){
        Emprunt emprunt = empruntRepository.findById(empruntId).orElseThrow(() -> new RessourceNotFoundException("Emprunt", empruntId));
        if (emprunt.getStatut() == StatutEmprunt.RENDU){
            throw new EmpruntDejaRenduException("Ce livre a déjà été rendu");
        }
        emprunt.setStatut(StatutEmprunt.RENDU);
        return empruntRepository.save(emprunt);
    }
}