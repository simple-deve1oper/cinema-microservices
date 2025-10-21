package dev.user.service;

import dev.user.service.impl.RabbitMQProducerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class RabbitMQProducerImplTest {
    final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    final RabbitMQProducer service = new RabbitMQProducerImpl(rabbitTemplate);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "taskExchange", "exchange_task");
        ReflectionTestUtils.setField(service, "userEmailVerifiedTaskRoutingKey", "task_user_email_verified_routing_key");
        ReflectionTestUtils.setField(service, "userDeleteInactiveTaskRoutingKey", "task_user_delete_inactive_routing_key");
        ReflectionTestUtils.setField(service, "timeEmailVerified", 2);
        ReflectionTestUtils.setField(service, "timeDeleteInactive", "0 0/5 * * * ?");
    }

    @Test
    void sendMessageEmailVerified() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        service.sendMessageEmailVerified(UUID.randomUUID().toString());
        service.sendMessageEmailVerified(UUID.randomUUID().toString());

        Mockito
                .verify(rabbitTemplate, Mockito.times(2))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }

    @Test
    void sendMessageDeleteInactive() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        service.sendMessageDeleteInactive();
        service.sendMessageDeleteInactive();

        Mockito
                .verify(rabbitTemplate, Mockito.times(2))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }
}
