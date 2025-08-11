package dev.gateway.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;

/**
 * Класс для описания ошибок
 * @param code - код
 * @param message - сообщение
 * @param dateTime - дата и время
 */
@Schema(
        name = "ApiErrorResponse",
        description = "DTO для описания ошибок"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        @Schema(name = "code", description = "Код")
        Integer code,
        @Schema(name = "message", description = "Сообщение")
        String message,
        @Schema(name = "dateTime", description = "Дата и время")
        ZonedDateTime dateTime
) {
    public ApiErrorResponse(Integer code, String message) {
        this(code, message, ZonedDateTime.now());
    }
}