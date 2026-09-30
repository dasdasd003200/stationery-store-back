package com.stationerystore.stationery_store.shared.pagination;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/** Page request shared by every paginated query (GraphQL input `PageInput`). Page is 0-based. */
public record PageInput(
        @Min(value = 0, message = "La página no puede ser negativa") Integer page,
        @Min(value = 1, message = "El tamaño de página debe ser al menos 1")
        @Max(value = PageInput.MAX_SIZE, message = "El tamaño de página no puede superar " + PageInput.MAX_SIZE)
        Integer size) {

    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 100;

    public static PageInput orDefault(PageInput input) {
        return input != null ? input : new PageInput(0, DEFAULT_SIZE);
    }

    public Pageable toPageable(Sort sort) {
        return PageRequest.of(page != null ? page : 0, size != null ? size : DEFAULT_SIZE, sort);
    }
}
