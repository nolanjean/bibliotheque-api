package com.bibliotheque.bibliotheque_api.service;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembreService {

    private final MembreRepository membreRepository;
    private final EmpruntRepository empruntRepository;
    private final PasswordEncoder passwordEncoder;

    public MembreService(MembreRepository membreRepository, EmpruntRepository empruntRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.empruntRepository = empruntRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Membre> listerTousLesMembres(){
        return membreRepository.findAll();
    }

    public Membre trouverParId(Long id){
        return membreRepository.findById(id).orElseThrow(() -> new RessourceNotFoundException("Membre", id));
    }

    public Membre creerMembre(RegisterRequest request){
        if (membreRepository.findByEmail(request.email()).isPresent()){
            throw new EmailDejaUtiliseException("Un compte existe déjà avec cet email");
        }
        Membre membre = new Membre();
        membre.setNom(request.nom());
        membre.setEmail(request.email());
        membre.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        membre.setRole(Role.MEMBRE);
        return membreRepository.save(membre);
    }

    public Membre mettreAJour(Long id, Membre membreModifie){
        Membre membreExistant = trouverParId(id);

        if (membreModifie.getNom() != null) {
            membreExistant.setNom(membreModifie.getNom());
        }
        if (membreModifie.getEmail() != null) {
            membreExistant.setEmail(membreModifie.getEmail());
        }
        if (membreModifie.getMotDePasse() != null) {
            membreExistant.setMotDePasse(passwordEncoder.encode(membreModifie.getMotDePasse()));
        }

        return membreRepository.save(membreExistant);
    }

    public void supprimer(Long id){
        Membre membre = trouverParId(id);

        List<Emprunt> listLivreEmprunter = empruntRepository.findByMembreIdAndStatut(id, StatutEmprunt.EN_COURS);
        if (!listLivreEmprunter.isEmpty()){
            throw new MembrePossedeEmpruntsException("Livre encore emprunté");
        }
        membreRepository.delete(membre);
    }
}