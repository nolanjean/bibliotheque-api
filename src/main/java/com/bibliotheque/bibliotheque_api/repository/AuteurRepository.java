package com.bibliotheque.bibliotheque_api.repository;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuteurRepository extends JpaRepository<Auteur, Long>, JpaSpecificationExecutor<Auteur> { }
