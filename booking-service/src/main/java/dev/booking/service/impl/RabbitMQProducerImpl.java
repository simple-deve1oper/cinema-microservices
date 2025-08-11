package dev.booking.service.impl;

import dev.booking.service.RabbitMQProducer;
import dev.library.domain.rabbitmq.ActionType;
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
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.receipt.exchange.name}")
    private String receiptExchange;
    @Value("${rabbitmq.receipt.routing-key.name.creation}")
    private String creationReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.name.update}")
    private String updateReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.name.update-status}")
    private String updateStatusReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.name.delete}")
    private String deleteReceiptRoutingKey;

    @Override
    public <T> void sendMessage(T message, ActionType type) {
        switch (type) {
            case CREATE -> rabbitTemplate.convertAndSend(receiptExchange, creationReceiptRoutingKey, message);
            case UPDATE -> rabbitTemplate.convertAndSend(receiptExchange, updateReceiptRoutingKey, message);
            case UPDATE_STATUS -> rabbitTemplate.convertAndSend(receiptExchange, updateStatusReceiptRoutingKey, message);
            case DELETE -> rabbitTemplate.convertAndSend(receiptExchange, deleteReceiptRoutingKey, message);
        }
    }
}
