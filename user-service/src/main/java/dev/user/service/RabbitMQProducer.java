package dev.user.service;

/**
 * Интерфейс для описания абстрактных методов по отправке данных в шину данных
 */
public interface RabbitMQProducer {
    void sendMessageEmailVerified(String id);
    void sendMessageDeleteInactive();
}
