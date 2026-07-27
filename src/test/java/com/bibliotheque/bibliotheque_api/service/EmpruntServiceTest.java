package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.exception.LimiteEmpruntException;
import com.bibliotheque.bibliotheque_api.exception.LivreIndisponibleException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;

class EmpruntServiceTest {

    private EmpruntRepository empruntRepository;
    private MembreRepository membreRepository;
    private LivreRepository livreRepository;
    private EmpruntService empruntService;

    @BeforeEach
    void setUp() {
        empruntRepository = mock(EmpruntRepository.class);
        membreRepository = mock(MembreRepository.class);
        livreRepository = mock(LivreRepository.class);
        empruntService = new EmpruntService(empruntRepository, membreRepository, livreRepository);
    }

    @Test
    void emprunterLivre_devraitReussir_siConditionsRespectees() {
        // Arrange
        Membre membre = new Membre();
        membre.setId(1L);

        Livre livre = new Livre();
        livre.setId(1L);
        livre.setNombreExemplaires(3);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(empruntRepository.findByMembreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(List.of());
        when(empruntRepository.findByLivreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(List.of());
        when(empruntRepository.save(any(Emprunt.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Emprunt resultat = empruntService.emprunterLivre(1L, 1L);

        // Assert
        assertNotNull(resultat);
        assertEquals(StatutEmprunt.EN_COURS, resultat.getStatut());
    }

    @Test
    void emprunterLivre_devraitLeverException_siLimiteAtteinte() {
        // Arrange
        Membre membre = new Membre();
        membre.setId(1L);

        List<Emprunt> troisEmprunts = List.of(new Emprunt(), new Emprunt(), new Emprunt());

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));
        when(empruntRepository.findByMembreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(troisEmprunts);

        // Act + Assert
        assertThrows(LimiteEmpruntException.class, () -> {
            empruntService.emprunterLivre(1L, 1L);
        });
    }

    @Test
    void emprunterLivre_devraitLeverException_siLivreIndisponible(){

        Membre membre = new Membre();
        membre.setId(1L);

        Livre livre = new Livre();
        livre.setId(1L);
        livre.setNombreExemplaires(2);

        when(membreRepository.findById(1L)).thenReturn(Optional.of(membre));
        when(empruntRepository.findByMembreIdAndStatut(1l,StatutEmprunt.EN_COURS)).thenReturn(List.of());
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(empruntRepository.findByLivreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(List.of(new Emprunt(), new Emprunt()));

        // Act + Assert
        assertThrows(LivreIndisponibleException.class, () -> {
            empruntService.emprunterLivre(1L, 1L);
        });

    }



}
