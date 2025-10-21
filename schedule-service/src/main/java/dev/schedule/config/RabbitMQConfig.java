package dev.schedule.config;

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

    @Value("${rabbitmq.task.queue.session.before-start}")
    private String sessionBeforeStartTaskQueue;
    @Value("${rabbitmq.task.routing-key.session.before-start}")
    private String sessionBeforeStartTaskRoutingKey;

    @Value("${rabbitmq.task.queue.session.disable-by-finished}")
    private String sessionDeleteTaskQueue;
    @Value("${rabbitmq.task.routing-key.session.disable-by-finished}")
    private String sessionDeleteTaskRoutingKey;

    @Value("${rabbitmq.task.queue.session.disable-by-finished}")
    private String sessionDisableByFinishedTaskQueue;
    @Value("${rabbitmq.task.routing-key.session.disable-by-finished}")
    private String sessionDisableByFinishedTaskRoutingKey;

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

    @Value("${rabbitmq.session.exchange}")
    private String sessionExchange;

    @Value("${rabbitmq.session.queue.disable-by-finished}")
    private String disableByFinishedSessionQueue;
    @Value("${rabbitmq.session.routing-key.disable-by-finished}")
    private String disableByFinishedSessionRoutingKey;

    @Value("${rabbitmq.session.queue.place.update-available}")
    private String updateAvailablePlaceSessionQueue;
    @Value("${rabbitmq.session.routing-key.place.update-available}")
    private String updateAvailablePlaceSessionRoutingKey;

    @Value("${rabbitmq.booking.exchange}")
    private String bookingExchange;

    @Value("${rabbitmq.booking.queue.check-by-session}")
    private String checkBySessionBookingQueue;
    @Value("${rabbitmq.booking.routing-key.check-by-session}")
    private String checkBySessionBookingRoutingKey;

    @Bean
    public Exchange taskExchange() {
        return new DirectExchange(taskExchange);
    }

    @Bean
    public Queue userEmailVerifiedTaskQueue() {
        return new Queue(userEmailVerifiedTaskQueue);
    }

    @Bean
    public Binding userEmailVerifiedTaskRoutingKey() {
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
    public Binding userDeleteInactiveTaskRoutingKey() {
        return BindingBuilder
                .bind(userDeleteInactiveTaskQueue())
                .to(taskExchange())
                .with(userDeleteInactiveTaskRoutingKey)
                .noargs();
    }

    @Bean
    public Queue sessionBeforeStartTaskQueue() {
        return new Queue(sessionBeforeStartTaskQueue);
    }

    @Bean
    public Binding sessionStartTaskBinding() {
        return BindingBuilder
                .bind(sessionBeforeStartTaskQueue())
                .to(taskExchange())
                .with(sessionBeforeStartTaskRoutingKey)
                .noargs();
    }

    @Bean
    public Queue sessionDeleteTaskQueue() {
        return new Queue(sessionDeleteTaskQueue);
    }

    @Bean
    public Binding sessionDeleteTaskBinding() {
        return BindingBuilder
                .bind(sessionDeleteTaskQueue())
                .to(taskExchange())
                .with(sessionDeleteTaskRoutingKey)
                .noargs();
    }

    @Bean
    public Queue sessionDisableByFinishedTaskQueue() {
        return new Queue(sessionDisableByFinishedTaskQueue);
    }

    @Bean
    public Binding sessionDisableByFinishedTaskBinding() {
        return BindingBuilder
                .bind(sessionDisableByFinishedTaskQueue())
                .to(taskExchange())
                .with(sessionDisableByFinishedTaskRoutingKey)
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
    public Binding emailVerifiedUserRoutingKey() {
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
    public Binding deleteInactiveUserRoutingKey() {
        return BindingBuilder
                .bind(deleteInactiveUserQueue())
                .to(userExchange())
                .with(deleteInactiveUserRoutingKey)
                .noargs();
    }

    @Bean
    public Exchange sessionExchange() {
        return new DirectExchange(sessionExchange);
    }

    @Bean
    public Queue disableByFinishedSessionQueue() {
        return new Queue(disableByFinishedSessionQueue);
    }

    @Bean
    public Binding disableByFinishedSessionBinding() {
        return BindingBuilder
                .bind(disableByFinishedSessionQueue())
                .to(sessionExchange())
                .with(disableByFinishedSessionRoutingKey)
                .noargs();
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
