package com.bibliotheque.bibliotheque_api.specification;

import com.bibliotheque.bibliotheque_api.entity.Membre;
import org.springframework.data.jpa.domain.Specification;

public class MembreSpecs {

    public static Specification<Membre> nomOuEmailContient(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("nom")), like),
                    cb.like(cb.lower(root.get("email")), like));
        };
    }
}