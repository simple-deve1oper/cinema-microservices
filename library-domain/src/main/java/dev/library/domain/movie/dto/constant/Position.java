package dev.library.domain.movie.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания позиций участников фильма
 */
@Schema(
        name = "AgeRating",
        description = "Перечисление для описания позиций участников фильма"
)
public enum Position {
    DIRECTOR("Режиссёр"), ACTOR("Актёр");

    private final String value;

    Position(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}