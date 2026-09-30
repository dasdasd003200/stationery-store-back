package com.stationerystore.stationery_store.modules.auth.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.stationerystore.stationery_store.shared.pagination.SearchPatterns;

import jakarta.persistence.criteria.Predicate;

public final class UserSpecifications {

    private UserSpecifications() {
    }

    /**
     * @param search case-insensitive text matched against username, full name and email
     * @param role   exact role, or null for any
     * @param active exact status, or null for any
     */
    public static Specification<User> matching(String search, Role role, Boolean active) {
        String pattern = SearchPatterns.contains(search);
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (pattern != null) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("username")), pattern, SearchPatterns.ESCAPE),
                        cb.like(cb.lower(root.get("fullName")), pattern, SearchPatterns.ESCAPE),
                        cb.like(cb.lower(cb.coalesce(root.get("email"), "")), pattern, SearchPatterns.ESCAPE)));
            }
            if (role != null) predicates.add(cb.equal(root.get("role"), role));
            if (active != null) predicates.add(cb.equal(root.get("active"), active));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
