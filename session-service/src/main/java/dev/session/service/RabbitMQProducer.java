package dev.session.service;

import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.rabbitmq.constant.ScheduleType;

import java.time.OffsetDateTime;

/**
 * Интерфейс для описания абстрактных методов по отправке данных в шину данных
 */
public interface RabbitMQProducer {
    /**
     * Отправка сообщения
     * @param sessionId - идентификатор сеанса
     * @param dateTime - дата и время
     * @param actionType - перечисление типа {@link ActionType}
     * @param scheduleType - перечисление типа {@link ScheduleType}
     */
    void sendMessage(String sessionId, OffsetDateTime dateTime, ActionType actionType, ScheduleType scheduleType);

    /**
     * Отправка сообщения
     * @param sessionId - идентификатор сеанса
     */
    void sendMessage(String sessionId);
}
