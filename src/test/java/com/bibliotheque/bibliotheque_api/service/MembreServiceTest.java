package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.RegisterRequest;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.enums.Role;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.EmailDejaUtiliseException;
import com.bibliotheque.bibliotheque_api.exception.MembrePossedeEmpruntsException;
import com.bibliotheque.bibliotheque_api.exception.RessourceNotFoundException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MembreServiceTest {

    private MembreRepository membreRepository;
    private EmpruntRepository empruntRepository;
    private PasswordEncoder passwordEncoder;
    private MembreService membreService;

    @BeforeEach
    void setUp(){
        membreRepository = mock(MembreRepository.class);
        empruntRepository = mock(EmpruntRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        membreService = new MembreService(membreRepository,empruntRepository,passwordEncoder);
    }

    @Test
    void creerMembre_DevraitLeverExceptionEmailExistant(){
        //Arrange
        RegisterRequest request = new RegisterRequest("Test","Test@gmail.com","");
        Membre membre = new Membre();
        membre.setEmail("Test@gmail.com");

        when(membreRepository.findByEmail("Test@gmail.com")).thenReturn(Optional.of(membre));

        assertThrows(EmailDejaUtiliseException.class, () -> {
            membreService.creerMembre(request);
        });
    }

    @Test
    void creerMembre_DevraitReussir(){
        RegisterRequest request = new RegisterRequest("Jean Dupont", "jean@email.com","motdepasse123");

        when(membreRepository.findByEmail("jean@email.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("motdepasse123")).thenReturn("hashSimule123");
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        Membre resultat = membreService.creerMembre(request);

        assertEquals("jean@email.com", resultat.getEmail());
        assertEquals("hashSimule123", resultat.getMotDePasse());
        assertEquals(Role.MEMBRE, resultat.getRole());
    }

    @Test
    void supprimerMembre_DevraitLeverException_LivreEmprunte(){
        Emprunt emprunt = new Emprunt();
        emprunt.setStatut(StatutEmprunt.EN_COURS);

        Membre membre = new Membre();
        membre.setId(1L);
        membre.setEmprunts(List.of(emprunt));

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));
        when(empruntRepository.findByMembreIdAndStatut(1L,StatutEmprunt.EN_COURS)).thenReturn(List.of(emprunt));

        assertThrows(MembrePossedeEmpruntsException.class, () -> {
            membreService.supprimer(1L);
        });
    }

    @Test
    void trouverParId_devraitLeverException_siIntrouvable(){
        //Arrange
        when(membreRepository.findById(1L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(RessourceNotFoundException.class, () -> {
            membreService.trouverParId(1L);
        });
    }

    @Test
    void mettreAJour_devraitModifierNomEtEmail_siFournis(){
        //Arrange
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setNom("Ancien nom");
        membreExistant.setEmail("ancien@email.com");

        Membre membreModifie = new Membre();
        membreModifie.setNom("Nouveau nom");
        membreModifie.setEmail("nouveau@email.com");

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        //Act
        Membre resultat = membreService.mettreAJour(1L, membreModifie);

        //Assert
        assertEquals("Nouveau nom", resultat.getNom());
        assertEquals("nouveau@email.com", resultat.getEmail());
    }

    @Test
    void supprimerMembre_devraitReussir_siAucunEmpruntEnCours(){
        //Arrange
        Membre membre = new Membre();
        membre.setId(1L);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));
        when(empruntRepository.findByMembreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(List.of());

        //Act
        membreService.supprimer(1L);

        //Assert
        verify(membreRepository).delete(membre);
    }
}
