package com.bibliotheque.bibliotheque_api.specification;

import com.bibliotheque.bibliotheque_api.entity.Auteur;
import org.springframework.data.jpa.domain.Specification;

public class AuteurSpecs {

    public static Specification<Auteur> nomContient(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            return cb.like(cb.lower(root.get("nom")), "%" + q.toLowerCase() + "%");
        };
    }
}