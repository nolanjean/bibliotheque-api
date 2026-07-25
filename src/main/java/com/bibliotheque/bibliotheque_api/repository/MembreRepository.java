package com.bibliotheque.bibliotheque_api.repository;

import com.bibliotheque.bibliotheque_api.entity.Membre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembreRepository extends JpaRepository<Membre, Long> {
    Optional<Membre> findByEmail(String email);
}
