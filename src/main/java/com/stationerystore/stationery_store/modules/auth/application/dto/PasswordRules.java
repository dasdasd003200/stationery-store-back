package com.stationerystore.stationery_store.modules.auth.application.dto;

/** Shared password policy used by every input that sets a password. */
final class PasswordRules {

    static final int MIN = 8;
    /** BCrypt ignores bytes beyond 72. */
    static final int MAX = 72;
    static final String PATTERN = "^(?=.*[A-Za-z])(?=.*\\d).+$";
    static final String MESSAGE = "La contraseña debe tener al menos 8 caracteres e incluir letras y números";

    private PasswordRules() {
    }
}
