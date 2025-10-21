package dev.library.domain.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO для отправки сообщения о выполнении задачи
 * @param data - данные
 * @param additionalProperties - дополнительные параметры
 */
public record TaskResponse(
        @Schema(name = "data", description = "Данные")
        Map<String, Object> data,
        @Schema(name = "additionalProperties", description = "Дополнительные параметры")
        Map<String, Object> additionalProperties
) {
    public TaskResponse(Map<String, Object> data) {
        this(data, null);
    }
}
