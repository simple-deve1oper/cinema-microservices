package dev.library.domain.session.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания форматов фильма
 */
@Schema(
        name = "MovieFormat",
        description = "Перечисление для описания форматов фильма"
)
public enum MovieFormat {
    TWO_D("2D"), THREE_D("3D");

    private final String value;

    MovieFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
