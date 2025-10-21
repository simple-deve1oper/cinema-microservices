package dev.user.service.impl;

import dev.library.domain.rabbitmq.constant.RabbitMQMessage;
import dev.library.domain.schedule.dto.TaskRequest;
import dev.user.service.RabbitMQProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
    @Value("${rabbitmq.task.routing-key.user.email-verified}")
    private String userEmailVerifiedTaskRoutingKey;
    @Value("${rabbitmq.task.routing-key.user.delete-inactive}")
    private String userDeleteInactiveTaskRoutingKey;
    @Value("${time.email-verified}")
    private Integer timeEmailVerified;
    @Value("${time.delete-inactive}")
    private String timeDeleteInactive;


    @Override
    public void sendMessageEmailVerified(String id) {
        log.debug("Started sendMessageEmailVerified(String id) with id = {}", id);
        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(timeEmailVerified);
        long milliseconds = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        TaskRequest request = new TaskRequest(id, milliseconds);
        log.debug("Create object TaskRequest with parameters name = {} and milliseconds = {}", id, milliseconds);
        rabbitTemplate.convertAndSend(taskExchange, userEmailVerifiedTaskRoutingKey, request);
        log.debug(RabbitMQMessage.SENDING_MESSAGE.getMessage(), taskExchange, userEmailVerifiedTaskRoutingKey);
    }

    @Override
    public void sendMessageDeleteInactive() {
        log.debug("Started sendMessageDeleteInactive()");
        TaskRequest request = new TaskRequest("Deleting inactive users", timeDeleteInactive);
        log.debug("Create object TaskRequest with parameters cron = {}", timeDeleteInactive);
        rabbitTemplate.convertAndSend(taskExchange, userDeleteInactiveTaskRoutingKey, request);
        log.debug(RabbitMQMessage.SENDING_MESSAGE.getMessage(), taskExchange, userDeleteInactiveTaskRoutingKey);
    }
}
