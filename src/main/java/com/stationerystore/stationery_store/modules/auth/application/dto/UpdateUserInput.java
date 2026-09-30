package com.stationerystore.stationery_store.modules.auth.application.dto;

import com.stationerystore.stationery_store.modules.auth.domain.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserInput(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        @Pattern(regexp = PersonNameRules.PATTERN, message = PersonNameRules.MESSAGE)
        String fullName,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String email,

        @NotNull(message = "El rol es obligatorio")
        Role role) {
}
