package dev.receipt.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQNotificationConfig {
    @Value("${rabbitmq.notification.queue.name.creation}")
    private String creationNotificationQueue;
    @Value("${rabbitmq.notification.queue.name.update}")
    private String updateNotificationQueue;
    @Value("${rabbitmq.notification.queue.name.update-status}")
    private String updateStatusNotificationQueue;
    @Value("${rabbitmq.notification.queue.name.delete}")
    private String deleteNotificationQueue;
    @Value("${rabbitmq.notification.exchange.name}")
    private String notificationExchange;
    @Value("${rabbitmq.notification.routing-key.name.creation}")
    private String creationNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.update}")
    private String updateNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.update-status}")
    private String updateStatusNotificationRoutingKey;
    @Value("${rabbitmq.notification.routing-key.name.delete}")
    private String deleteNotificationRoutingKey;

    @Value("${rabbitmq.receipt.queue.name.creation}")
    private String creationReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.update}")
    private String updateReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.update-status}")
    private String updateStatusReceiptQueue;
    @Value("${rabbitmq.receipt.queue.name.delete}")
    private String deleteReceiptQueue;

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
    public Queue creationNotificationQueue() {
        return new Queue(creationNotificationQueue);
    }

    @Bean
    public Queue updateNotificationQueue() {
        return new Queue(updateNotificationQueue);
    }

    @Bean
    public Queue updateStatusNotificationQueue() {
        return new Queue(updateStatusNotificationQueue);
    }

    @Bean
    public Queue deleteNotificationQueue() {
        return new Queue(deleteNotificationQueue);
    }

    @Bean
    public Exchange notificationExchange() {
        return new DirectExchange(notificationExchange);
    }

    @Bean
    public Binding creationNotificationBinding() {
        return BindingBuilder
                .bind(creationNotificationQueue())
                .to(notificationExchange())
                .with(creationNotificationRoutingKey)
                .noargs();
    }

    @Bean
    public Binding updateNotificationBinding() {
        return BindingBuilder
                .bind(updateNotificationQueue())
                .to(notificationExchange())
                .with(updateNotificationRoutingKey)
                .noargs();
    }

    @Bean
    public Binding updateStatusNotificationBinding() {
        return BindingBuilder
                .bind(updateStatusNotificationQueue())
                .to(notificationExchange())
                .with(updateStatusNotificationRoutingKey)
                .noargs();
    }

    @Bean
    public Binding deleteNotificationBinding() {
        return BindingBuilder
                .bind(deleteNotificationQueue())
                .to(notificationExchange())
                .with(deleteNotificationRoutingKey)
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
