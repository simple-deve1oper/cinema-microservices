package dev.library.domain.dictionary.country.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для получения данных о стране
 * @param id - идентификатор
 * @param code - код
 * @param name - наименование
 */
@Schema(
        name = "CountryResponse",
        description = "DTO для получения данных о стране"
)
public record CountryResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "code", description = "Код")
        String code,
        @Schema(name = "name", description = "Наименование")
        String name
) {}
