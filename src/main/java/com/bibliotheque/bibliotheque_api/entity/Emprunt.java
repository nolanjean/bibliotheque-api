package com.bibliotheque.bibliotheque_api.entity;

import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "emprunt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Emprunt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "livre_id")
    private Livre livre;

    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;

    @Enumerated(EnumType.STRING)
    private StatutEmprunt statut;
}
