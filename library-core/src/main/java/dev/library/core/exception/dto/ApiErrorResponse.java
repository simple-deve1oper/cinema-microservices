package dev.library.core.exception.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Класс для описания ошибок
 * @param code - код
 * @param message - сообщение
 * @param fields - поля с ошибками валидации и сообщения к ним
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
        @Schema(name = "fields", description = "Поля с ошибками валидации и сообщения к ним",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Map<String, String> fields,
        @Schema(name = "dateTime", description = "Дата и время")
        ZonedDateTime dateTime
) {
    public ApiErrorResponse(Integer code, String message) {
        this(code, message, null, ZonedDateTime.now());
    }

    public ApiErrorResponse(Integer code, String message, Map<String, String> fields) {
        this(code, message, fields, ZonedDateTime.now());
    }
}