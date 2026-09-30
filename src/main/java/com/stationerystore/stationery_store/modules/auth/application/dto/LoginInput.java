package com.stationerystore.stationery_store.modules.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginInput(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @NotBlank(message = "La contraseña es obligatoria") String password) {
}
