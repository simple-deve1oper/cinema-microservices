package dev.library.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;

/**
 * DTO для получения данных о сеансе
 * @param id - идентификатор
 * @param movieId - идентификатор фильма
 * @param movieFormat - формат фильма
 * @param hall - зал
 * @param dateTime - дата и время
 * @param available - доступность
 */
@Schema(
        name = "SessionResponse",
        description = "DTO для получения данных о сеансе"
)
public record SessionResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "movieId", description = "Идентификатор фильма")
        Long movieId,
        @Schema(name = "movieFormat", description = "Формат фильма")
        String movieFormat,
        @Schema(name = "hall", description = "Зал")
        Integer hall,
        @Schema(name = "dateTime", description = "Дата и время")
        OffsetDateTime dateTime,
        @Schema(name = "available", description = "Доступность")
        Boolean available
) {}
