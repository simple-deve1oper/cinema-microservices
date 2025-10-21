package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.schedule.service.RabbitMQProducer;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;

/**
 * Запуск задачи о завершении доступности сеанса
 */
public record SessionDisableByFinishedJob(
        RabbitMQProducer rabbitMQProducer,
        Scheduler scheduler
) implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
            String sessionId = jobDetail.getName();
            rabbitMQProducer.sendMessage(sessionId, ScheduleType.SESSION_DISABLE_BY_FINISHED);
            scheduler.deleteJob(jobDetail.getKey());
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }
}
