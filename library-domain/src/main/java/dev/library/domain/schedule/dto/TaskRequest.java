package dev.library.domain.schedule.dto;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * DTO для отправки сообщения о создании задачи
 * @param name - наименование
 * @param millisecondsToStart - время начала выполнения в миллисекундах
 * @param cron - время выполнения в cron-выражении
 * @param additionalProperties - дополнительные параметры
 */
public record TaskRequest(
        @Schema(name = "name", description = "Наименование")
        String name,
        @Schema(name = "millisecondsToStart", description = "Время начала выполнения в миллисекундах")
        Long millisecondsToStart,
        @Schema(name = "cron", description = "Время выполнения в cron-выражении")
        String cron,
        @Schema(name = "additionalProperties", description = "Дополнительные параметры")
        Map<String, Object> additionalProperties
) {
    public TaskRequest(String name, long millisecondsToStart) {
        this(name, millisecondsToStart, null, null);
    }

    public TaskRequest(String name, String cron) {
        this(name, null, cron, null);
    }

    public TaskRequest(String name, long millisecondsToStart, Map<String, Object> additionalProperties) {
        this(name, millisecondsToStart, null, additionalProperties);
    }

    public TaskRequest(String name) {
        this(name, null, null, null);
    }
}
