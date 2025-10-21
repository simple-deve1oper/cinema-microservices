package dev.library.domain.rabbitmq.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для указания очереди для задач в шине данных
 */
@Schema(
        name = "ScheduleType",
        description = "Перечисление для указания очереди для задач в шине данных"
)
public enum ScheduleType {
    USER_EMAIL_VERIFIED, DELETE_USERS_INACTIVE, BOOKING_CHECK_BEFORE_START_SESSION,
    SESSION_DELETE, SESSION_DISABLE_BY_FINISHED
}
