package dev.library.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для получения данных о месте на сеансе
 * @param id - идентификатор
 * @param sessionId - идентификатор сеанса
 * @param row - ряд
 * @param number - номер
 * @param price - цена
 * @param available - доступность
 */
@Schema(
        name = "PlaceResponse",
        description = "DTO для получения данных о месте на сеансе"
)
public record PlaceResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "sessionId", description = "Идентификатор сеанса")
        Long sessionId,
        @Schema(name = "row", description = "Ряд")
        Integer row,
        @Schema(name = "number", description = "Номер")
        Integer number,
        @Schema(name = "price", description = "Цена")
        String price,
        @Schema(name = "available", description = "Доступность")
        Boolean available
) {}
