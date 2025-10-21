package dev.session.service.impl;

import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.rabbitmq.constant.RabbitMQMessage;
import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.library.domain.schedule.dto.TaskRequest;
import dev.session.service.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Сервис, реализующий интерфейс {@link RabbitMQProducer}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMQProducerImpl implements RabbitMQProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.task.exchange}")
    private String taskExchange;
    @Value("${rabbitmq.task.routing-key.session.before-start}")
    private String sessionBeforeStartTaskRoutingKey;
    @Value("${rabbitmq.task.routing-key.session.delete}")
    private String sessionDeleteTaskRoutingKey;
    @Value("${rabbitmq.task.routing-key.session.disable-by-finished}")
    private String sessionDisableByFinishedTaskRoutingKey;
    @Value("${time.before-start}")
    private Integer timeBeforeStart;

    @Override
    public void sendMessage(String sessionId, OffsetDateTime dateTime, ActionType actionType, ScheduleType scheduleType) {
        log.debug("Started sendMessage(Long sessionId, OffsetDateTime dateTime, ActionType actionType, ScheduleType scheduleType) with sessionIs = {}, dateTime = {}, actionType = {} and scheduledTime = {}", sessionId, dateTime, actionType, scheduleType);
        if (scheduleType == ScheduleType.BOOKING_CHECK_BEFORE_START_SESSION) {
            dateTime = dateTime.minusMinutes(timeBeforeStart);
        }
        long milliseconds = dateTime.toInstant().toEpochMilli();
        Map<String, Object> additionalProperties = Map.of("type", actionType);
        TaskRequest request = new TaskRequest(sessionId, milliseconds, additionalProperties);
        String textForDebug = "Sending message {} to exchange %s via routing key %s";
        switch (scheduleType) {
            case BOOKING_CHECK_BEFORE_START_SESSION -> {
                textForDebug = textForDebug.formatted(taskExchange, sessionBeforeStartTaskRoutingKey);
                rabbitTemplate.convertAndSend(taskExchange, sessionBeforeStartTaskRoutingKey, request);
            }
            case SESSION_DISABLE_BY_FINISHED -> {
                textForDebug = textForDebug.formatted(taskExchange, sessionDisableByFinishedTaskRoutingKey);
                rabbitTemplate.convertAndSend(taskExchange, sessionDisableByFinishedTaskRoutingKey, request);
            }
        }
        log.debug(textForDebug, request);
    }

    @Override
    public void sendMessage(String sessionId) {
        log.debug("Started sendMessage(Long sessionId, OffsetDateTime dateTime) with sessionId = {}", sessionId);
        rabbitTemplate.convertAndSend(taskExchange, sessionDeleteTaskRoutingKey, sessionId);
        log.debug(RabbitMQMessage.SENDING_MESSAGE.getMessage(), sessionId, taskExchange, sessionDeleteTaskRoutingKey);
    }
}
