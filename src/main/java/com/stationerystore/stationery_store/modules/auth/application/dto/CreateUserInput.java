package com.stationerystore.stationery_store.modules.auth.application.dto;

import com.stationerystore.stationery_store.modules.auth.domain.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserInput(
        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 3, max = 50, message = "El usuario debe tener entre 3 y 50 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "El usuario solo admite letras, números, punto, guion y guion bajo")
        String username,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        @Pattern(regexp = PersonNameRules.PATTERN, message = PersonNameRules.MESSAGE)
        String fullName,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo no puede superar 150 caracteres")
        String email,

        @NotBlank(message = PasswordRules.MESSAGE)
        @Size(min = PasswordRules.MIN, max = PasswordRules.MAX, message = PasswordRules.MESSAGE)
        @Pattern(regexp = PasswordRules.PATTERN, message = PasswordRules.MESSAGE)
        String password,

        @NotNull(message = "El rol es obligatorio")
        Role role) {
}
