package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.MembreUpdateRequest;
import com.bibliotheque.bibliotheque_api.dto.request.RegisterRequest;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.Role;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.EmailDejaUtiliseException;
import com.bibliotheque.bibliotheque_api.exception.MembrePossedeEmpruntsException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import com.bibliotheque.bibliotheque_api.specification.MembreSpecs;
import com.bibliotheque.bibliotheque_api.util.TriUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class MembreService {

    private static final Logger logger = LoggerFactory.getLogger(MembreService.class);
    private final MembreRepository membreRepository;
    private final EmpruntRepository empruntRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Set<String> TRIS_AUTORISES = Set.of("id", "nom", "email");

    public MembreService(MembreRepository membreRepository, EmpruntRepository empruntRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.empruntRepository = empruntRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Membre> listerTousLesMembres(){
        return membreRepository.findAll();
    }

    public Membre trouverParId(Long id){
        return membreRepository.findById(id).orElseThrow(() -> {
            logger.warn("Membre introuvable, ID {}", id);
            return new RessourceNotFoundException("Membre", id);
        });
    }

    public Membre creerMembre(RegisterRequest request){
        if (membreRepository.findByEmail(request.email()).isPresent()){
            logger.warn("Email déjà utiliser : {}", request.email());
            throw new EmailDejaUtiliseException("Un compte existe déjà avec cet email");
        }
        Membre membre = new Membre();
        membre.setNom(request.nom());
        membre.setEmail(request.email());
        membre.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        membre.setRole(Role.MEMBRE);

        Membre membreCreer = membreRepository.save(membre);
        logger.info("Membre crée avec succès, ID {}", membreCreer.getId());

        return membreCreer;
    }

    public Membre mettreAJour(Long id, MembreUpdateRequest request) {
        Membre membreExistant = trouverParId(id);

        if (request.nom() != null) {
            membreExistant.setNom(request.nom());
        }
        if (request.email() != null && !request.email().equalsIgnoreCase(membreExistant.getEmail())) {
            if (membreRepository.findByEmail(request.email()).isPresent()) {
                throw new EmailDejaUtiliseException("Un compte existe déjà avec cet email");
            }
            membreExistant.setEmail(request.email());
        }
        if (request.motDePasse() != null) {
            membreExistant.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }

        Membre membreMisAJour = membreRepository.save(membreExistant);
        logger.info("Membre mis à jour, ID {}", membreMisAJour.getId());
        return membreMisAJour;
    }

    @Transactional
    public void supprimer(Long id){
        Membre membre = trouverParId(id);

        List<Emprunt> listLivreEmprunter = empruntRepository.findByMembreIdAndStatut(id, StatutEmprunt.EN_COURS);
        if (!listLivreEmprunter.isEmpty()){
            logger.warn("Membre possède encore des emprunt, ID {}", id);
            throw new MembrePossedeEmpruntsException("Livre encore emprunté");
        }
        empruntRepository.deleteByMembreId(id);   // <-- nouvelle ligne
        membreRepository.delete(membre);
        logger.info("Membre supprimer avec succès, ID {}", id);
    }

    public Page<Membre> rechercher(String q, Pageable pageable) {
        Specification<Membre> spec = Specification.where(MembreSpecs.nomOuEmailContient(q));
        return membreRepository.findAll(spec, TriUtils.securiser(pageable, TRIS_AUTORISES));
    }
}