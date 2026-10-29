package com.bibliotheque.bibliotheque_api.repository;

import com.bibliotheque.bibliotheque_api.entity.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Long>, JpaSpecificationExecutor<Livre> {
    Optional<Livre> findByIsbn(String isbn);
}
