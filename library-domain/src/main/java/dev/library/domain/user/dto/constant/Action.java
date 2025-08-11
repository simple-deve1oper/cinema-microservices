package dev.library.domain.user.dto.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для описания действий с аккаунтами пользователей
 */
@Schema(
        name = "Action",
        description = "Перечисление для описания действий с аккаунтами пользователей"
)
public enum Action {
    UPDATE_PASSWORD
}
