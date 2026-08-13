package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.IsbnDejaExistantException;
import com.bibliotheque.bibliotheque_api.exception.LivreNonRenduException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.LivreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LivreServiceTest {

    private LivreRepository livreRepository;
    private EmpruntRepository empruntRepository;
    private LivreService livreService;

    @BeforeEach
    void setUp(){
        livreRepository = mock(LivreRepository.class);
        empruntRepository = mock(EmpruntRepository.class);
        livreService = new LivreService(livreRepository,empruntRepository);
    }

    @Test
    void livreSupprimer_devraitLeverException_SiStatutRendu(){
        //Arange
        Livre livre = new Livre();
        livre.setId(1L);

        Emprunt emprunt = new Emprunt();
        emprunt.setId(1L);
        emprunt.setLivre(livre);
        emprunt.setStatut(StatutEmprunt.EN_COURS);

        //when ??
        when(livreRepository.findById(1L)).thenReturn(Optional.of(livre));
        when(empruntRepository.findByLivreIdAndStatut(1L, StatutEmprunt.EN_COURS)).thenReturn(List.of(emprunt));

        assertThrows(LivreNonRenduException.class, () -> {
            livreService.supprimer(1L);
        });
    }

    @Test
    void creerLivre_devraitLeverException_siIsbnExistant() {
        //Arrange
        Livre livre = new Livre();
        livre.setTitre("Titre test");
        livre.setIsbn("978-2-00-000000-1");
        livre.setNombreExemplaires(3);

        Livre livreExistant = new Livre();
        livreExistant.setIsbn("978-2-00-000000-1");

        when(livreRepository.findByIsbn("978-2-00-000000-1")).thenReturn(Optional.of(livreExistant));

        //Act + assert
        assertThrows(IsbnDejaExistantException.class, () -> {
            livreService.creerLivre(livre);
        });
    }

    @Test
    void creerLivre_devraitReussir(){
        //Arrange
        Auteur auteur = new Auteur();
        auteur.setId(1L);

        Livre livre = new Livre();
        livre.setTitre("Titre test");
        livre.setIsbn("978-2-00-000000-1");
        livre.setNombreExemplaires(3);
        livre.setAuteur(auteur);

        //when
        when(livreRepository.findByIsbn("978-2-00-000000-1")).thenReturn(Optional.empty());
        when(livreRepository.save(any(Livre.class))).thenAnswer(i -> i.getArgument(0));

        //Act
        Livre resultat = livreService.creerLivre(livre);

        //Assert
        assertEquals("Titre test", resultat.getTitre());

    }

    @Test
    void mettreAJour_devraitModifierSeulementLeTitre_siSeulTitreFourni() {
        // Arrange
        Livre livreExistant = new Livre();
        livreExistant.setId(1L);
        livreExistant.setTitre("Ancien titre");
        livreExistant.setIsbn("978-2-00-000000-1");
        livreExistant.setNombreExemplaires(5);

        Livre livreModifie = new Livre();
        livreModifie.setTitre("Nouveau titre");
        // isbn et nombreExemplaires volontairement non renseignés

        when(livreRepository.findById(1L)).thenReturn(Optional.of(livreExistant));
        when(livreRepository.save(any(Livre.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Livre resultat = livreService.mettreAJour(1L, livreModifie);

        // Assert
        assertEquals("Nouveau titre", resultat.getTitre());
        assertEquals("978-2-00-000000-1", resultat.getIsbn());
        assertEquals(5, resultat.getNombreExemplaires());
    }

}
