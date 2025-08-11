package dev.library.domain.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для получения данных о жанре фильма
 * @param id - идентификатор
 * @param name - наименование
 */
@Schema(
        name = "GenreResponse",
        description = "DTO для получения данных о жанре фильма"
)
public record GenreResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "name", description = "Наименование")
        String name
) {}
