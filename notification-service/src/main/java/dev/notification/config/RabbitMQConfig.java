package dev.notification.config;

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
    @Value("${rabbitmq.notification.exchange}")
    private String notificationExchange;

    @Value("${rabbitmq.notification.queue.creation}")
    private String creationNotificationQueue;
    @Value("${rabbitmq.notification.routing-key.creation}")
    private String creationNotificationRoutingKey;

    @Value("${rabbitmq.notification.queue.update}")
    private String updateNotificationQueue;
    @Value("${rabbitmq.notification.routing-key.update}")
    private String updateNotificationRoutingKey;

    @Value("${rabbitmq.notification.queue.update-status}")
    private String updateStatusNotificationQueue;
    @Value("${rabbitmq.notification.routing-key.update-status}")
    private String updateStatusNotificationRoutingKey;

    @Value("${rabbitmq.notification.queue.delete}")
    private String deleteNotificationQueue;
    @Value("${rabbitmq.notification.routing-key.delete}")
    private String deleteNotificationRoutingKey;

    @Bean
    public Exchange notificationExchange() {
        return new DirectExchange(notificationExchange);
    }

    @Bean
    public Queue creationNotificationQueue() {
        return new Queue(creationNotificationQueue);
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
    public Queue updateNotificationQueue() {
        return new Queue(updateNotificationQueue);
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
    public Queue updateStatusNotificationQueue() {
        return new Queue(updateStatusNotificationQueue);
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
    public Queue deleteNotificationQueue() {
        return new Queue(deleteNotificationQueue);
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