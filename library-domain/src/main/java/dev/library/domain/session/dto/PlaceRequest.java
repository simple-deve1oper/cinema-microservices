package dev.library.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO для создания/обновления данных по месту на сеансе
 * @param sessionId - идентификатор сеанса
 * @param row - ряд
 * @param number - номер
 * @param price - цена
 * @param available - доступность
 */
@Schema(
        name = "PlaceRequest",
        description = "DTO для создания/обновления данных по месту на сеансе"
)
public record PlaceRequest(
        @Schema(name = "sessionId", description = "Идентификатор сеанса")
        @NotNull(message = "Идентификатор сеанса не может быть пустым")
        @Min(value = 1, message = "Минимальное значение идентификатора сеанса 1")
        Long sessionId,
        @Schema(name = "row", description = "Ряд")
        @NotNull(message = "Номер ряда в зале не может быть пустым")
        @Min(value = 1, message = "Минимальное значение номера ряда 1")
        Integer row,
        @Schema(name = "number", description = "Номер")
        @NotNull(message = "Номер места в зале не может быть пустым")
        @Min(value = 1, message = "Минимальное значение номера места 1")
        Integer number,
        @Schema(name = "price", description = "Цена")
        @NotNull(message = "Цена за место не может быть пустой")
        BigDecimal price,
        @Schema(name = "available", description = "Доступность")
        @NotNull(message = "Доступность места не может быть пустым")
        Boolean available
) {}
