package dev.notification.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
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