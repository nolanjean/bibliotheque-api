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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setNom("Ancien nom");
        membreExistant.setEmail("ancien@email.com");

        MembreUpdateRequest request = new MembreUpdateRequest("Nouveau nom", "nouveau@email.com", null);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(membreRepository.findByEmail("nouveau@email.com")).thenReturn(Optional.empty());
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        Membre resultat = membreService.mettreAJour(1L, request);

        assertEquals("Nouveau nom", resultat.getNom());
        assertEquals("nouveau@email.com", resultat.getEmail());
    }

    @Test
    void mettreAJour_devraitLeverException_siEmailDejaUtiliseParUnAutreMembre(){
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setEmail("ancien@email.com");

        Membre autreMembre = new Membre();
        autreMembre.setId(2L);
        autreMembre.setEmail("pris@email.com");

        MembreUpdateRequest request = new MembreUpdateRequest(null, "pris@email.com", null);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(membreRepository.findByEmail("pris@email.com")).thenReturn(Optional.of(autreMembre));

        assertThrows(EmailDejaUtiliseException.class, () -> {
            membreService.mettreAJour(1L, request);
        });
        verify(membreRepository, never()).save(any(Membre.class));
    }

    @Test
    void mettreAJour_neDevraitPasVerifierLeDoublon_siEmailInchange(){
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setEmail("jean@email.com");

        MembreUpdateRequest request = new MembreUpdateRequest(null, "JEAN@email.com", null);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        Membre resultat = membreService.mettreAJour(1L, request);

        assertEquals("jean@email.com", resultat.getEmail());
        verify(membreRepository, never()).findByEmail(anyString());
    }

    @Test
    void mettreAJour_devraitEncoderLeMotDePasse_siFourni(){
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setMotDePasse("ancienHash");

        MembreUpdateRequest request = new MembreUpdateRequest(null, null, "nouveaumdp123");

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(passwordEncoder.encode("nouveaumdp123")).thenReturn("nouveauHash");
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        Membre resultat = membreService.mettreAJour(1L, request);

        assertEquals("nouveauHash", resultat.getMotDePasse());
    }

    @Test
    void mettreAJour_neDevraitPasModifierLeRoleNiLeMotDePasse_siNonFournis(){
        Membre membreExistant = new Membre();
        membreExistant.setId(1L);
        membreExistant.setNom("Ancien nom");
        membreExistant.setRole(Role.MEMBRE);
        membreExistant.setMotDePasse("hashExistant");

        MembreUpdateRequest request = new MembreUpdateRequest("Nouveau nom", null, null);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membreExistant));
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        Membre resultat = membreService.mettreAJour(1L, request);

        assertEquals(Role.MEMBRE, resultat.getRole());
        assertEquals("hashExistant", resultat.getMotDePasse());
        verify(passwordEncoder, never()).encode(anyString());
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
        verify(empruntRepository).deleteByMembreId(1L);
    }

    @Test
    void rechercher_devraitIgnorerUnTriSurLeMotDePasse() {
        when(membreRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        membreService.rechercher(null, PageRequest.of(0, 10, Sort.by("motDePasse")));

        verify(membreRepository).findAll(any(Specification.class), captor.capture());
        assertTrue(captor.getValue().getSort().isUnsorted());
    }

    @Test
    void rechercher_devraitRetournerLaPageDuRepository() {
        Membre membre = new Membre();
        membre.setId(1L);
        membre.setNom("Jean");
        Page<Membre> page = new PageImpl<>(List.of(membre));

        when(membreRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<Membre> resultat = membreService.rechercher("jean", PageRequest.of(0, 10));

        assertEquals(1, resultat.getTotalElements());
        assertEquals("Jean", resultat.getContent().get(0).getNom());
    }

    @Test
    void rechercher_devraitConserverUnTriAutorise() {
        when(membreRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);

        membreService.rechercher(null, PageRequest.of(0, 10, Sort.by("nom")));

        verify(membreRepository).findAll(any(Specification.class), captor.capture());
        assertNotNull(captor.getValue().getSort().getOrderFor("nom"));
    }
}