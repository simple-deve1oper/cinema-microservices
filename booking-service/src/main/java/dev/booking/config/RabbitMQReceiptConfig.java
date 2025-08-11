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
public class RabbitMQReceiptConfig {
    @Value("${rabbitmq.receipt.queue.name.creation}")
    private String creationReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.update}")
    private String updateReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.update-status}")
    private String updateStatusReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.delete}")
    private String deleteReceiptQueue;
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

    @Bean
    public Queue creationReceiptQueue() {
        return new Queue(creationReceiptQueue);
    }

    @Bean
    public Queue updateReceiptQueue() {
        return new Queue(updateReceiptQueue);
    }

    @Bean
    public Queue updateStatusReceiptQueue() {
        return new Queue(updateStatusReceiptQueue);
    }

    @Bean
    public Queue deleteReceiptQueue() {
        return new Queue(deleteReceiptQueue);
    }

    @Bean
    public Exchange receiptExchange() {
        return new DirectExchange(receiptExchange);
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
    public Binding updateReceiptBinding() {
        return BindingBuilder
                .bind(updateReceiptQueue())
                .to(receiptExchange())
                .with(updateReceiptRoutingKey)
                .noargs();
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
    public Binding deleteReceiptBinding() {
        return BindingBuilder
                .bind(deleteReceiptQueue())
                .to(receiptExchange())
                .with(deleteReceiptRoutingKey)
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