package dev.library.domain.schedule.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания состояния пользователей
 */
@Schema(
        name = "UserState",
        description = "Перечисление для описания состояния пользователей"
)
public enum UserState {
    DELETE_INACTIVE
}
