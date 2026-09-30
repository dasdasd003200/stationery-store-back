package com.stationerystore.stationery_store.shared.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SearchPatternsTest {

    @Test
    void wrapsLowercasedTermForContainsMatch() {
        assertThat(SearchPatterns.contains("  Juan ")).isEqualTo("%juan%");
    }

    @Test
    void escapesWildcardsSoUserInputIsLiteral() {
        assertThat(SearchPatterns.contains("50%_a\\b")).isEqualTo("%50\\%\\_a\\\\b%");
    }

    @Test
    void blankMeansNoFilter() {
        assertThat(SearchPatterns.contains("   ")).isNull();
        assertThat(SearchPatterns.contains(null)).isNull();
    }
}
