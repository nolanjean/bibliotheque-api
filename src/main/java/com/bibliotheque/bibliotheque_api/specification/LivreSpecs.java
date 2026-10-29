package com.bibliotheque.bibliotheque_api.specification;

import com.bibliotheque.bibliotheque_api.entity.Livre;
import org.springframework.data.jpa.domain.Specification;

public class LivreSpecs {
    public static Specification<Livre> titreOuIsbnContient(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("titre")), like),
                    cb.like(cb.lower(root.get("isbn")), like));
        };
    }
    public static Specification<Livre> auteurEgal(Long auteurId) {
        return (root, query, cb) -> auteurId == null ? null
                : cb.equal(root.get("auteur").get("id"), auteurId);
    }
}