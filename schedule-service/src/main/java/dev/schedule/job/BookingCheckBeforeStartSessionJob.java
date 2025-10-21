package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.schedule.service.RabbitMQProducer;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;

/**
 * Запуск задачи о проверке бронирований по идентификатору сеанса
 */
public record BookingCheckBeforeStartSessionJob(
        RabbitMQProducer rabbitMQProducer,
        Scheduler scheduler
) implements Job {
    @Override
    public void execute(JobExecutionContext context) {
        try {
            JobDetailImpl jobDetail = (JobDetailImpl) context.getJobDetail();
            String sessionId = jobDetail.getName();
            rabbitMQProducer.sendMessage(sessionId, ScheduleType.BOOKING_CHECK_BEFORE_START_SESSION);
            scheduler.deleteJob(jobDetail.getKey());
        } catch (SchedulerException e) {
            throw new IllegalStateException(e);
        }
    }
}
