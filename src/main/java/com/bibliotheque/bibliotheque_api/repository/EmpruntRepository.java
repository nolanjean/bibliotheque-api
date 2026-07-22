package com.bibliotheque.bibliotheque_api.repository;

import com.bibliotheque.bibliotheque_api.entity.Emprunt;
import com.bibliotheque.bibliotheque_api.enums.StatutEmprunt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    List<Emprunt> findByMembreIdAndStatut(Long membreId, StatutEmprunt statut);
    List<Emprunt> findByLivreIdAndStatut(Long livreId, StatutEmprunt statut);
}
