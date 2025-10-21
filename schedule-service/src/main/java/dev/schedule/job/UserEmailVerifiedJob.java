package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.schedule.service.RabbitMQProducer;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;

/**
 * Запуск задачи о деактивации пользователя, если он не подтвердил электронную почту
 */
public record UserEmailVerifiedJob(
        RabbitMQProducer rabbitMQProducer,
        Scheduler scheduler
) implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
            String id = jobDetail.getName();
            rabbitMQProducer.sendMessage(id, ScheduleType.USER_EMAIL_VERIFIED);
            scheduler.deleteJob(jobDetail.getKey());
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }
}
