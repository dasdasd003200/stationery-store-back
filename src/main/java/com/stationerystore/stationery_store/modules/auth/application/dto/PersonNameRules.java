package com.stationerystore.stationery_store.modules.auth.application.dto;

/** Person names: letters (any language, incl. accents), spaces, apostrophes, dots and hyphens. */
final class PersonNameRules {

    static final String PATTERN = "^\\p{L}[\\p{L} .'-]*$";
    static final String MESSAGE = "El nombre solo puede contener letras y espacios";

    private PersonNameRules() {
    }
}
