package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.AuteurCreateRequest;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.exception.AuteurPossedeLivresException;
import com.bibliotheque.bibliotheque_api.repository.AuteurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AuteurServiceTest {

    private AuteurRepository auteurRepository;
    private AuteurService auteurService;

    @BeforeEach
    void setUp(){
        auteurRepository = mock(AuteurRepository.class);
        auteurService = new AuteurService(auteurRepository);
    }

    @Test
    void supprimerAuteur_devraitLeverExceptionSiEncoreLivre(){
        //Arrange
        Auteur auteur = new Auteur();
        auteur.setId(1L);
        Livre livre = new Livre();
        auteur.setLivres(List.of(livre));

        when(auteurRepository.findById(1L)).thenReturn(Optional.of(auteur));

        assertThrows(AuteurPossedeLivresException.class, () -> auteurService.supprimer(1L));

    }

    @Test

    void supprimerDevraitReussir(){

        //Arrange

        Auteur auteur = new Auteur();

        auteur.setId(1L);

        auteur.setLivres(List.of());

        when(auteurRepository.findById(1L)).thenReturn(Optional.of(auteur));

        auteurService.supprimer(1L);

        verify(auteurRepository).delete(auteur);

    }

    @Test
    void creerAuteurDevraitReussir(){
        Auteur auteur = new Auteur();
        auteur.setNom("John");

        when(auteurRepository.save(any(Auteur.class))).thenAnswer(i -> i.getArgument(0));

        Auteur resultat = auteurService.creerAuteur(auteur);

        assertEquals("John", resultat.getNom());
    }

}
