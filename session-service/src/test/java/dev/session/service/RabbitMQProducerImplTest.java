package dev.session.service;

import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.session.service.impl.RabbitMQProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;

@ExtendWith(MockitoExtension.class)
public class RabbitMQProducerImplTest {
    final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    final RabbitMQProducer service = new RabbitMQProducerImpl(rabbitTemplate);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "taskExchange", "exchange_task");
        ReflectionTestUtils.setField(service, "sessionBeforeStartTaskRoutingKey", "task_session_before_start_queue");
        ReflectionTestUtils.setField(service, "sessionDeleteTaskRoutingKey", "task_session_delete_queue");
        ReflectionTestUtils.setField(service, "sessionDisableByFinishedTaskRoutingKey", "task_session_disable_by_finished_queue");
        ReflectionTestUtils.setField(service, "timeBeforeStart", 2);
    }

    @Test
    void sendMessageCase() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        service.sendMessage("1", OffsetDateTime.now(), ActionType.SESSION_START_CREATE,
                ScheduleType.BOOKING_CHECK_BEFORE_START_SESSION);
        service.sendMessage("2", OffsetDateTime.now(), ActionType.SESSION_START_UPDATE,
                ScheduleType.SESSION_DISABLE_BY_FINISHED);

        Mockito
                .verify(rabbitTemplate, Mockito.times(2))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }

    @Test
    void sendMessageDelete() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        service.sendMessage("1");
        service.sendMessage("2");

        Mockito
                .verify(rabbitTemplate, Mockito.times(2))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }
}
