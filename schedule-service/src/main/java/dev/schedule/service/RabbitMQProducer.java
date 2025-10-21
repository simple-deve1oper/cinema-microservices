package dev.schedule.service;

import dev.library.domain.rabbitmq.constant.ScheduleType;

/**
 * Интерфейс для описания абстрактных методов по отправке данных в шину данных
 */
public interface RabbitMQProducer {
    /**
     * Отправка сообщения
     * @param message - объект
     * @param type - перечисление типа {@link ScheduleType}
     * @param <T> - тип данных
     */
    <T> void sendMessage(T message, ScheduleType type);
}
