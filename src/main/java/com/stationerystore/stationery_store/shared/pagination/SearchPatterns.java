package com.stationerystore.stationery_store.shared.pagination;

import java.util.Locale;

/** Helpers to build safe, case-insensitive "contains" patterns for SQL LIKE. */
public final class SearchPatterns {

    public static final char ESCAPE = '\\';

    private SearchPatterns() {
    }

    /** "50%_off" → "%50\%\_off%" (user input never acts as a wildcard). Null if blank. */
    public static String contains(String term) {
        if (term == null || term.isBlank()) return null;
        String escaped = term.trim().toLowerCase(Locale.ROOT)
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
        return "%" + escaped + "%";
    }
}
