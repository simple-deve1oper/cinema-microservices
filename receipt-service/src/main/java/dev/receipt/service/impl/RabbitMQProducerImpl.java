package dev.receipt.service.impl;

import dev.library.domain.rabbitmq.constant.ActionType;
import dev.receipt.service.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link RabbitMQProducer}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducerImpl implements RabbitMQProducer {
    @Value("${rabbitmq.notification.exchange}")
    private String notificationExchange;
    @Value("${rabbitmq.notification.routing-key.creation}")
    private String creationNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.update}")
    private String updateNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.update-status}")
    private String updateStatusNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.delete}")
    private String deleteNotificationRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    @Override
    public <T> void sendMessage(T message, ActionType type) {
        log.debug("Started sendMessage(T message, ActionType type) with message = {} and type = {}", message, type);
        String textForDebug = "Sending message {} to exchange %s via routing key %s";
        switch (type) {
            case CREATE -> {
                textForDebug = textForDebug.formatted(notificationExchange, creationNotificationRoutingKey);
                rabbitTemplate.convertAndSend(notificationExchange, creationNotificationRoutingKey, message);
            }
            case UPDATE -> {
                textForDebug = textForDebug.formatted(notificationExchange, updateNotificationRoutingKey);
                rabbitTemplate.convertAndSend(notificationExchange, updateNotificationRoutingKey, message);
            }
            case UPDATE_STATUS -> {
                textForDebug = textForDebug.formatted(notificationExchange, updateStatusNotificationRoutingKey);
                rabbitTemplate.convertAndSend(notificationExchange, updateStatusNotificationRoutingKey, message);
            }
            case DELETE ->{
                textForDebug = textForDebug.formatted(notificationExchange, deleteNotificationRoutingKey);
                rabbitTemplate.convertAndSend(notificationExchange, deleteNotificationRoutingKey, message);
            }
        }
        log.debug(textForDebug, message);
    }
}
