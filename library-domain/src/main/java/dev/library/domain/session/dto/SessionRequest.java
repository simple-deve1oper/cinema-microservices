package dev.library.domain.session.dto;

import dev.library.domain.session.dto.constant.MovieFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

/**
 * DTO для создания/обновления данных по сеансу
 * @param movieId - идентификатор фильма
 * @param movieFormat - формат фильма
 * @param hall - зал
 * @param dateTime - дата и время
 * @param available - доступность
 */
@Schema(
        name = "SessionRequest",
        description = "DTO для создания/обновления данных по сеансу"
)
public record SessionRequest(
        @Schema(name = "movieId", description = "Формат фильма")
        @NotNull(message = "Идентификатор фильма не может быть пустым")
        @Min(value = 1, message = "Минимальное значение идентификатора фильма 1")
        Long movieId,
        @Schema(name = "movieFormat", description = "Формат фильма")
        @NotNull(message = "Формат фильма не может быть пустым")
        MovieFormat movieFormat,
        @Schema(name = "hall", description = "Зал")
        @NotNull(message = "Номер зала не может быть пустым")
        @Min(value = 1, message = "Минимальное значение номера зала 1")
        Integer hall,
        @Schema(name = "dateTime", description = "Дата и время")
        @NotNull(message = "Дата и время сеанса не может быть пустым")
        OffsetDateTime dateTime,
        @Schema(name = "available", description = "Доступность")
        @NotNull(message = "Доступность сеанса не может быть пустой")
        Boolean available
) {}
