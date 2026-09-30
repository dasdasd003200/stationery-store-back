package com.stationerystore.stationery_store.modules.auth.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.stationerystore.stationery_store.modules.auth.domain.Role;
import com.stationerystore.stationery_store.modules.auth.domain.User;

/** Read model exposed through the API. Never exposes the password hash. */
public record UserView(
        UUID id,
        String username,
        String fullName,
        String email,
        Role role,
        List<String> permissions,
        boolean active,
        Instant lastLoginAt,
        Instant createdAt,
        Instant updatedAt) {

    public static UserView from(User user) {
        return new UserView(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getEmail(),
                user.getRole(),
                user.getRole().permissionCodes(),
                user.isActive(),
                user.getLastLoginAt(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }
}
