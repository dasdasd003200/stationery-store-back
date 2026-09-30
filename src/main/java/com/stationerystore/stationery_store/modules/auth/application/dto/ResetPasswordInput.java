package com.stationerystore.stationery_store.modules.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordInput(
        @NotBlank(message = PasswordRules.MESSAGE)
        @Size(min = PasswordRules.MIN, max = PasswordRules.MAX, message = PasswordRules.MESSAGE)
        @Pattern(regexp = PasswordRules.PATTERN, message = PasswordRules.MESSAGE)
        String newPassword) {
}
