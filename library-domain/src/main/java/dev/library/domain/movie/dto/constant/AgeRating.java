package dev.library.domain.movie.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания возрастных рейтингов
 */
@Schema(
        name = "AgeRating",
        description = "Перечисление для описания возрастных рейтингов"
)
public enum AgeRating {
    ZERO("0+"), SIX("6+"), TWELVE("12+"),
    SIXTEEN("16+"), EIGHTEEN("18+");

    private final String value;

    AgeRating(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}