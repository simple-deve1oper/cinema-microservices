package dev.user.config;

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
    @Value("${rabbitmq.task.exchange}")
    private String taskExchange;

    @Value("${rabbitmq.task.queue.user.email-verified}")
    private String userEmailVerifiedTaskQueue;
    @Value("${rabbitmq.task.routing-key.user.email-verified}")
    private String userEmailVerifiedTaskRoutingKey;

    @Value("${rabbitmq.task.queue.user.delete-inactive}")
    private String userDeleteInactiveTaskQueue;
    @Value("${rabbitmq.task.routing-key.user.delete-inactive}")
    private String userDeleteInactiveTaskRoutingKey;

    @Value("${rabbitmq.user.exchange}")
    private String userExchange;

    @Value("${rabbitmq.user.queue.email-verified}")
    private String emailVerifiedUserQueue;
    @Value("${rabbitmq.user.routing-key.email-verified}")
    private String emailVerifiedUserRoutingKey;

    @Value("${rabbitmq.user.queue.delete-inactive}")
    private String deleteInactiveUserQueue;
    @Value("${rabbitmq.user.routing-key.delete-inactive}")
    private String deleteInactiveUserRoutingKey;

    @Bean
    public Exchange taskExchange() {
        return new DirectExchange(taskExchange);
    }

    @Bean
    public Queue userEmailVerifiedTaskQueue() {
        return new Queue(userEmailVerifiedTaskQueue);
    }

    @Bean
    public Binding userEmailVerifiedTaskBinding() {
        return BindingBuilder
                .bind(userEmailVerifiedTaskQueue())
                .to(taskExchange())
                .with(userEmailVerifiedTaskRoutingKey)
                .noargs();
    }

    @Bean
    public Queue userDeleteInactiveTaskQueue() {
        return new Queue(userDeleteInactiveTaskQueue);
    }

    @Bean
    public Binding userDeleteInactiveTaskBinding() {
        return BindingBuilder
                .bind(userDeleteInactiveTaskQueue())
                .to(taskExchange())
                .with(userDeleteInactiveTaskRoutingKey)
                .noargs();
    }

    @Bean
    public Exchange userExchange() {
        return new DirectExchange(userExchange);
    }

    @Bean
    public Queue emailVerifiedUserQueue() {
        return new Queue(emailVerifiedUserQueue);
    }

    @Bean
    public Binding emailVerifiedUserBinding() {
        return BindingBuilder
                .bind(emailVerifiedUserQueue())
                .to(userExchange())
                .with(emailVerifiedUserRoutingKey)
                .noargs();
    }

    @Bean
    public Queue deleteInactiveUserQueue() {
        return new Queue(deleteInactiveUserQueue);
    }

    @Bean
    public Binding deleteInactiveUserBinding() {
        return BindingBuilder
                .bind(deleteInactiveUserQueue())
                .to(userExchange())
                .with(deleteInactiveUserRoutingKey)
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
