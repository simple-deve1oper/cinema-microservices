package dev.receipt.service.impl;

import dev.library.domain.rabbitmq.ActionType;
import dev.receipt.service.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link RabbitMQProducer}
 */
@Service
@RequiredArgsConstructor
public class RabbitMQProducerImpl implements RabbitMQProducer {
    @Value("${rabbitmq.notification.exchange.name}")
    private String notificationExchange;
    @Value("${rabbitmq.notification.routing-key.name.creation}")
    private String creationNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.update}")
    private String updateNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.update-status}")
    private String updateStatusNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.delete}")
    private String deleteNotificationRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T> void sendMessage(T message, ActionType type) {
        switch (type) {
            case CREATE -> rabbitTemplate.convertAndSend(notificationExchange, creationNotificationRoutingKey, message);
            case UPDATE -> rabbitTemplate.convertAndSend(notificationExchange, updateNotificationRoutingKey, message);
            case UPDATE_STATUS -> rabbitTemplate.convertAndSend(notificationExchange, updateStatusNotificationRoutingKey, message);
            case DELETE -> rabbitTemplate.convertAndSend(notificationExchange, deleteNotificationRoutingKey, message);
        }
    }
}
