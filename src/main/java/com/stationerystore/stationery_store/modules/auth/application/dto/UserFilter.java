package com.stationerystore.stationery_store.modules.auth.application.dto;

import com.stationerystore.stationery_store.modules.auth.domain.Role;

import jakarta.validation.constraints.Size;

/** Optional criteria for the users list; null fields are ignored. */
public record UserFilter(
        @Size(max = 100, message = "La búsqueda no puede superar 100 caracteres") String search,
        Role role,
        Boolean active) {

    public static final UserFilter NONE = new UserFilter(null, null, null);
}
