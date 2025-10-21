package dev.schedule.service.impl;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.schedule.service.RabbitMQProducer;
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

    @Value("${rabbitmq.user.exchange}")
    private String userExchange;
    @Value("${rabbitmq.user.routing-key.email-verified}")
    private String emailVerifiedUserRoutingKey;
    @Value("${rabbitmq.user.routing-key.delete-inactive}")
    private String deleteInactiveUserRoutingKey;
    @Value("${rabbitmq.session.exchange}")
    private String sessionExchange;
    @Value("${rabbitmq.session.routing-key.disable-by-finished}")
    private String disableByFinishedSessionRoutingKey;
    @Value("${rabbitmq.booking.exchange}")
    private String bookingExchange;
    @Value("${rabbitmq.booking.routing-key.check-by-session}")
    private String checkBySessionBookingRoutingKey;

    @Override
    public <T> void sendMessage(T message, ScheduleType type) {
        log.debug("Started sendMessage(T message, ScheduleType type) with message = {} and type = {}", message, type);
        String textForDebug = "Sending message {} to exchange %s via routing key %s";
        switch (type) {
            case USER_EMAIL_VERIFIED -> {
                textForDebug = textForDebug.formatted(userExchange, emailVerifiedUserRoutingKey);
                rabbitTemplate.convertAndSend(userExchange, emailVerifiedUserRoutingKey, message);
            }
            case DELETE_USERS_INACTIVE -> {
                textForDebug = textForDebug.formatted(userExchange, deleteInactiveUserRoutingKey);
                rabbitTemplate.convertAndSend(userExchange, deleteInactiveUserRoutingKey, message);
            }
            case BOOKING_CHECK_BEFORE_START_SESSION -> {
                textForDebug = textForDebug.formatted(bookingExchange, checkBySessionBookingRoutingKey);
                rabbitTemplate.convertAndSend(bookingExchange, checkBySessionBookingRoutingKey, message);
            }
            case SESSION_DISABLE_BY_FINISHED -> {
                textForDebug = textForDebug.formatted(sessionExchange, disableByFinishedSessionRoutingKey);
                rabbitTemplate.convertAndSend(sessionExchange, disableByFinishedSessionRoutingKey, message);
            }
        }
        log.debug(textForDebug, message);
    }
}
