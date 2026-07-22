package com.bibliotheque.bibliotheque_api.repository;

import com.bibliotheque.bibliotheque_api.entity.Livre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivreRepository extends JpaRepository<Livre, Long> {

}
