package dev.schedule.service;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.library.domain.schedule.dto.TaskResponse;
import dev.schedule.service.impl.RabbitMQProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Map;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RabbitMQProducerImplTest {
    final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    final RabbitMQProducer service = new RabbitMQProducerImpl(rabbitTemplate);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "userExchange", "exchange_user");
        ReflectionTestUtils.setField(service, "emailVerifiedUserRoutingKey", "user_email_verified_routing_key");
        ReflectionTestUtils.setField(service, "deleteInactiveUserRoutingKey", "user_delete_inactive_routing_key");
        ReflectionTestUtils.setField(service, "sessionExchange", "exchange_session");
        ReflectionTestUtils.setField(service, "disableByFinishedSessionRoutingKey", "disable_by_finished_session_routing_key");
        ReflectionTestUtils.setField(service, "bookingExchange", "exchange_booking");
        ReflectionTestUtils.setField(service, "checkBySessionBookingRoutingKey", "check_by_session_booking_routing_key");
    }

    @Test
    void sendMessage() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.any(), Mockito.any(ScheduleType.class));

        TaskResponse taskResponse = new TaskResponse(Map.of("name", "task"));
        service.sendMessage(taskResponse, ScheduleType.DELETE_USERS_INACTIVE);
        service.sendMessage(UUID.randomUUID().toString(), ScheduleType.USER_EMAIL_VERIFIED);
        service.sendMessage("1", ScheduleType.SESSION_DISABLE_BY_FINISHED);
        service.sendMessage("2", ScheduleType.BOOKING_CHECK_BEFORE_START_SESSION);

        Mockito
                .verify(rabbitTemplate, Mockito.times(4))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }
}
