package dev.library.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

/**
 * DTO для получения данных об изображении
 * @param id - идентификатор
 * @param movieId - идентификатор фильма
 * @param fileName - наименование файла
 * @param number - порядковый номер
 */
@Schema(
        name = "ImageResponse",
        description = "DTO для получения данных об изображении"
)
public record ImageResponse(
        @Schema(name = "id", description = "Идентификатор")
        UUID id,
        @Schema(name = "movieId", description = "Идентификатор фильма")
        Long movieId,
        @Schema(name = "fileName", description = "Наименование файла")
        String fileName,
        @Schema(name = "number", description = "Порядковый номер")
        Integer number
) {}
