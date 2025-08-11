package dev.booking.service;

import dev.library.domain.rabbitmq.ActionType;

/**
 * Интерфейс для описания абстрактных методов по отправке данных в шину данных
 */
public interface RabbitMQProducer {
    /**
     * Отправка сообщения
     * @param message - объект
     * @param type - перечисление типа {@link ActionType}
     * @param <T> - тип данных
     */
    <T> void sendMessage(T message, ActionType type);
}
