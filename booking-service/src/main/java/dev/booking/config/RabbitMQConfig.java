package dev.booking.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${rabbitmq.receipt.exchange}")
    private String receiptExchange;

    @Value("${rabbitmq.receipt.queue.creation}")
    private String creationReceiptQueue;
    @Value("${rabbitmq.receipt.routing-key.creation}")
    private String creationReceiptRoutingKey;

    @Value("${rabbitmq.receipt.queue.update}")
    private String updateReceiptQueue;
    @Value("${rabbitmq.receipt.routing-key.update}")
    private String updateReceiptRoutingKey;

    @Value("${rabbitmq.receipt.queue.update-status}")
    private String updateStatusReceiptQueue;
    @Value("${rabbitmq.receipt.routing-key.update-status}")
    private String updateStatusReceiptRoutingKey;

    @Value("${rabbitmq.receipt.queue.delete}")
    private String deleteReceiptQueue;
    @Value("${rabbitmq.receipt.routing-key.delete}")
    private String deleteReceiptRoutingKey;

    @Value("${rabbitmq.booking.exchange}")
    private String bookingExchange;

    @Value("${rabbitmq.booking.queue.check-by-session}")
    private String checkBySessionBookingQueue;
    @Value("${rabbitmq.booking.routing-key.check-by-session}")
    private String checkBySessionBookingRoutingKey;

    @Value("${rabbitmq.session.exchange}")
    private String sessionExchange;

    @Value("${rabbitmq.session.queue.place.update-available}")
    private String updateAvailablePlaceSessionQueue;
    @Value("${rabbitmq.session.routing-key.place.update-available}")
    private String updateAvailablePlaceSessionRoutingKey;

    @Bean
    public Exchange receiptExchange() {
        return new DirectExchange(receiptExchange);
    }

    @Bean
    public Queue creationReceiptQueue() {
        return new Queue(creationReceiptQueue);
    }

    @Bean
    public Binding creationReceiptBinding() {
        return BindingBuilder
                .bind(creationReceiptQueue())
                .to(receiptExchange())
                .with(creationReceiptRoutingKey)
                .noargs();
    }

    @Bean
    public Queue updateReceiptQueue() {
        return new Queue(updateReceiptQueue);
    }

    @Bean
    public Binding updateReceiptBinding() {
        return BindingBuilder
                .bind(updateReceiptQueue())
                .to(receiptExchange())
                .with(updateReceiptRoutingKey)
                .noargs();
    }

    @Bean
    public Queue updateStatusReceiptQueue() {
        return new Queue(updateStatusReceiptQueue);
    }

    @Bean
    public Binding updateStatusReceiptBinding() {
        return BindingBuilder
                .bind(updateStatusReceiptQueue())
                .to(receiptExchange())
                .with(updateStatusReceiptRoutingKey)
                .noargs();
    }

    @Bean
    public Queue deleteReceiptQueue() {
        return new Queue(deleteReceiptQueue);
    }

    @Bean
    public Binding deleteReceiptBinding() {
        return BindingBuilder
                .bind(deleteReceiptQueue())
                .to(receiptExchange())
                .with(deleteReceiptRoutingKey)
                .noargs();
    }

    @Bean
    public Exchange bookingExchange() {
        return new DirectExchange(bookingExchange);
    }

    @Bean
    public Queue checkBySessionBookingQueue() {
        return new Queue(checkBySessionBookingQueue);
    }

    @Bean
    public Binding checkBySessionBookingBinding() {
        return BindingBuilder
                .bind(checkBySessionBookingQueue())
                .to(bookingExchange())
                .with(checkBySessionBookingRoutingKey)
                .noargs();
    }

    @Bean
    public Exchange sessionExchange() {
        return new DirectExchange(sessionExchange);
    }

    @Bean
    public Queue updateAvailablePlaceSessionQueue() {
        return new Queue(updateAvailablePlaceSessionQueue);
    }

    @Bean
    public Binding updateAvailablePlaceSessionBinding() {
        return BindingBuilder
                .bind(updateAvailablePlaceSessionQueue())
                .to(sessionExchange())
                .with(updateAvailablePlaceSessionRoutingKey)
                .noargs();
    }

    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}