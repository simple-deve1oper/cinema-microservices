package dev.library.domain.rabbitmq.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Перечисление для шаблонов сообщений для брокера сообщений RabbitMQ
 */
@Schema(
        name = "RabbitMQMessage",
        description = "Перечисление для шаблонов сообщений для брокера сообщений RabbitMQ"
)
public enum RabbitMQMessage {
    SENDING_MESSAGE("Sending message {} to exchange {} via routing key {}");

    private final String message;

    RabbitMQMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
