package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.LivreCreateRequest;
import com.bibliotheque.bibliotheque_api.entity.Auteur;
import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.entity.Livre;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import com.bibliotheque.bibliotheque_api.exception.IsbnDejaExistantException;
import com.bibliotheque.bibliotheque_api.exception.LivreNonRenduException;
import com.bibliotheque.bibliotheque_api.repository.AuteurRepository;
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
    private AuteurRepository auteurRepository;
    private LivreService livreService;

    @BeforeEach
    void setUp(){
        livreRepository = mock(LivreRepository.class);
        empruntRepository = mock(EmpruntRepository.class);
        auteurRepository = mock(AuteurRepository.class);
        livreService = new LivreService(livreRepository,empruntRepository,auteurRepository);
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
        LivreCreateRequest request = new LivreCreateRequest("Titre test", "978-2-00-000000-1", 3, 1L);
        Livre livreExistant = new Livre();
        livreExistant.setIsbn("978-2-00-000000-1");

        when(livreRepository.findByIsbn("978-2-00-000000-1")).thenReturn(Optional.of(livreExistant));

        //Act + assert
        assertThrows(IsbnDejaExistantException.class, () -> {
            livreService.creerLivre(request);
        });
    }

    @Test
    void creerLivre_devraitReussir(){
        //Arrange
        LivreCreateRequest request = new LivreCreateRequest("Titre test", "978-2-00-000000-1", 3, 1L);
        Auteur auteur = new Auteur();
        auteur.setId(1L);

        //when
        when(livreRepository.findByIsbn("978-2-00-000000-1")).thenReturn(Optional.empty());
        when(auteurRepository.findById(1L)).thenReturn(Optional.of(auteur));
        when(livreRepository.save(any(Livre.class))).thenAnswer(i -> i.getArgument(0));

        //Act
        Livre resultat = livreService.creerLivre(request);

        //Assert
        assertEquals("Titre test", resultat.getTitre());

    }

}
