package dev.library.domain.rabbitmq.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для указания действий для заказов и квитанций в шине данных
 */
@Schema(
        name = "ActionType",
        description = "Перечисление для указания действий для заказов и квитанций в шине данных"
)
public enum ActionType {
    CREATE, UPDATE, UPDATE_STATUS, DELETE, SESSION_START_CREATE, SESSION_START_UPDATE
}
