package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.LivreUpdateRequest;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.IsbnDejaExistantException;
import com.bibliotheque.bibliotheque_api.exception.LivreNonRenduException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import com.bibliotheque.bibliotheque_api.specification.LivreSpecs;
import com.bibliotheque.bibliotheque_api.util.TriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class LivreService {

    private static final Logger logger = LoggerFactory.getLogger(LivreService.class);
    private final LivreRepository livreRepository;
    private final EmpruntRepository empruntRepository;
    private static final Set<String> TRIS_AUTORISES = Set.of("id", "titre", "isbn");


    public LivreService(LivreRepository livreRepository, EmpruntRepository empruntRepository) {
        this.livreRepository = livreRepository;
        this.empruntRepository = empruntRepository;
    }

    public Page<Livre> listerTousLesLivres(Pageable pageable){
        return livreRepository.findAll(pageable);
    }

    public Livre trouverParId(Long id){
        return livreRepository.findById(id).orElseThrow(() -> {
            logger.warn("Livre introuvable, ID {}", id);
            return new RessourceNotFoundException("Livre", id);
        });
    }

    public Livre creerLivre(Livre livre){
        logger.info("Tentative de création d'un livre avec ISBN {}", livre.getIsbn());

        if (livreRepository.findByIsbn(livre.getIsbn()).isPresent()){
            logger.warn("Echec création livre : ISBN {} déjà existant", livre.getIsbn());
            throw new IsbnDejaExistantException("Isbn déjà existant");
        }

        Livre livreSauvegarde = livreRepository.save(livre);
        logger.info("Livre crée avec succès, ID {}", livreSauvegarde.getId());

        return livreSauvegarde;
    }

    public Livre mettreAJour(Long id, LivreUpdateRequest request, Auteur auteur){
        Livre livreExistant = trouverParId(id);

        if (request.titre() != null) {
            livreExistant.setTitre(request.titre());
        }
        if (request.isbn() != null) {
            livreExistant.setIsbn(request.isbn());
        }
        if (request.nombreExemplaires() != null) {
            livreExistant.setNombreExemplaires(request.nombreExemplaires());
        }
        if (auteur != null) {
            livreExistant.setAuteur(auteur);
        }

        Livre livreMisAJour = livreRepository.save(livreExistant);
        logger.info("Livre mis a jour, ID {}", id);
        return livreMisAJour;
    }

    @Transactional
    public void supprimer(Long id){
        Livre livre = trouverParId(id);

        List<Emprunt> listLivreEmprunter = empruntRepository.findByLivreIdAndStatut(id, StatutEmprunt.EN_COURS);
        if (!listLivreEmprunter.isEmpty()){
            logger.warn("Echec livre encore non rendu : ID {}", id);
            throw new LivreNonRenduException("Livre encore non rendu");
        }

        empruntRepository.deleteByLivreId(id);   // <-- nouvelle ligne
        livreRepository.delete(livre);
        logger.info("Livre supprimé, ID {}", id);
    }


    public Page<Livre> rechercher(String q, Long auteurId, Pageable pageable) {
        Specification<Livre> spec = Specification
                .where(LivreSpecs.titreOuIsbnContient(q))
                .and(LivreSpecs.auteurEgal(auteurId));

        return livreRepository.findAll(spec, TriUtils.securiser(pageable, TRIS_AUTORISES));
    }
}