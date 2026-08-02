package com.bibliotheque.bibliotheque_api.service;

import com.bibliotheque.bibliotheque_api.dto.request.RegisterRequest;
import com.bibliotheque.bibliotheque_api.entity.Membre;
import com.bibliotheque.bibliotheque_api.exception.EmailDejaUtiliseException;
import com.bibliotheque.bibliotheque_api.repository.EmpruntRepository;
import com.bibliotheque.bibliotheque_api.repository.MembreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
