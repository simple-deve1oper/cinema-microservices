package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.library.domain.schedule.dto.TaskResponse;
import dev.library.domain.schedule.constant.UserState;
import dev.schedule.service.RabbitMQProducer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Map;

/**
 * Запуск задачи об удалении неактивных пользователей
 */
public record UserDeleteInactiveJob(
        RabbitMQProducer rabbitMQProducer
) implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        Map<String, Object> data = Map.of("userState", UserState.DELETE_INACTIVE);
        TaskResponse response = new TaskResponse(data);
        rabbitMQProducer.sendMessage(response, ScheduleType.DELETE_USERS_INACTIVE);
    }
}
