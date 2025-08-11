package dev.booking.service;

import dev.booking.service.impl.RabbitMQProducerImpl;
import dev.library.domain.rabbitmq.ActionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class RabbitMQProducerImplTest {
    final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);
    RabbitMQProducer service = new RabbitMQProducerImpl(rabbitTemplate);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "receiptExchange", "exchange_receipt_booking");
        ReflectionTestUtils.setField(service, "creationReceiptRoutingKey", "creation_receipt_routing_key");
        ReflectionTestUtils.setField(service, "updateReceiptRoutingKey", "update_receipt_routing_key");
        ReflectionTestUtils.setField(service, "deleteReceiptRoutingKey", "delete_receipt_routing_key");
    }

    @Test
    void sendMessage_create() {
        Mockito
                .doNothing()
                .when(rabbitTemplate)
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));

        service.sendMessage("Hello!", ActionType.CREATE);
        service.sendMessage("Hello!", ActionType.UPDATE);
        service.sendMessage("Hello!", ActionType.UPDATE_STATUS);
        service.sendMessage("Hello!", ActionType.DELETE);

        Mockito
                .verify(rabbitTemplate, Mockito.times(3))
                .convertAndSend(Mockito.anyString(), Mockito.anyString(), Mockito.any(Object.class));
    }
}
