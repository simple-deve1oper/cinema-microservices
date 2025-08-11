package dev.library.domain.file.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

/**
 * DTO для создания/обновления данных по изображению
 * @param movieId - идентификатор фильма
 * @param fileName - наименование файла
 * @param number - порядковый номер
 */
@Schema(
        name = "ImageRequest",
        description = "DTO для создания/обновления данных по изображению"
)
public record ImageRequest(
        @Schema(name = "movieId", description = "Идентификатор фильма")
        @NotNull(message = "Идентификатор фильма не может быть пустым")
        @Min(value = 1, message = "Минимальное значение идентификатора фильма 1")
        Long movieId,
        @Schema(name = "fileName", description = "Наименование файла")
        @NotBlank(message = "Наименование файла не может быть пустым")
        @Length(max = 255, message = "Наименование файла не может содержать более 255 символов")
        String fileName,
        @Schema(name = "number", description = "Порядковый номер")
        @NotNull(message = "Порядковый номер не может быть пустым")
        @Min(value = 1, message = "Минимальное значение порядкового номера 1")
        Integer number
) {}
