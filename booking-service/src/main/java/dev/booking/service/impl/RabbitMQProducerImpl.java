package dev.booking.service.impl;

import dev.booking.service.RabbitMQProducer;
import dev.library.domain.rabbitmq.constant.ActionType;
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
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.receipt.exchange}")
    private String receiptExchange;
    @Value("${rabbitmq.receipt.routing-key.creation}")
    private String creationReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.update}")
    private String updateReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.update-status}")
    private String updateStatusReceiptRoutingKey;
    @Value("${rabbitmq.receipt.routing-key.delete}")
    private String deleteReceiptRoutingKey;

    @Value("${rabbitmq.session.exchange}")
    private String sessionExchange;
    @Value("${rabbitmq.session.routing-key.place.update-available}")
    private String updateAvailablePlaceSessionRoutingKey;

    @Override
    public <T> void sendMessage(T message, ActionType type) {
        log.debug("Started sendMessage(T message, ActionType type) with message = {} and type = {}", message, type);
        String textForDebug = "Sending message {} to exchange %s via routing key %s";
        switch (type) {
            case CREATE -> {
                textForDebug = textForDebug.formatted(receiptExchange, creationReceiptRoutingKey);
                rabbitTemplate.convertAndSend(receiptExchange, creationReceiptRoutingKey, message);
            }
            case UPDATE -> {
                textForDebug = textForDebug.formatted(receiptExchange, updateReceiptRoutingKey);
                rabbitTemplate.convertAndSend(receiptExchange, updateReceiptRoutingKey, message);
            }
            case UPDATE_STATUS -> {
                textForDebug = textForDebug.formatted(receiptExchange, updateStatusReceiptRoutingKey);
                rabbitTemplate.convertAndSend(receiptExchange, updateStatusReceiptRoutingKey, message);
            }
            case DELETE -> {
                textForDebug = textForDebug.formatted(receiptExchange, deleteReceiptRoutingKey);
                rabbitTemplate.convertAndSend(receiptExchange, deleteReceiptRoutingKey, message);
            }
        }
        log.debug(textForDebug, message);
    }

    @Override
    public <T> void sendMessage(T message) {
        log.debug("Started sendMessage(T message) with message = {}", message);
        rabbitTemplate.convertAndSend(sessionExchange, updateAvailablePlaceSessionRoutingKey, message);
        log.debug("Sending message {} to exchange {} via routing key {}", message, sessionExchange, updateReceiptRoutingKey);
    }
}
