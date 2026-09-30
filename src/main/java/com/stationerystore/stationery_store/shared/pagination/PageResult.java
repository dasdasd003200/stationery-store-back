package com.stationerystore.stationery_store.shared.pagination;

import java.util.List;
import java.util.function.Function;

import org.springframework.data.domain.Page;

/** Generic page returned by the API. Each module exposes it as `<Entity>Page` in its schema. */
public record PageResult<T>(List<T> items, PageInfo pageInfo) {

    public record PageInfo(int page, int size, long totalItems, int totalPages) {
    }

    public static <E, T> PageResult<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResult<>(
                page.getContent().stream().map(mapper).toList(),
                new PageInfo(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages()));
    }
}
