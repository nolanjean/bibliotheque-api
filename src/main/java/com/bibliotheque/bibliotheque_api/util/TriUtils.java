package com.bibliotheque.bibliotheque_api.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;

public final class TriUtils {

    private TriUtils() {}

    public static Pageable securiser(Pageable pageable, Set<String> champsAutorises) {
        List<Sort.Order> ordres = pageable.getSort().stream()
                .filter(o -> champsAutorises.contains(o.getProperty()))
                .toList();
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(ordres));
    }
}