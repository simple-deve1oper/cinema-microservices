package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.schedule.service.RabbitMQProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;

@ExtendWith(MockitoExtension.class)
public class SessionDisableByFinishedJobTest {
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final Scheduler scheduler = Mockito.mock(Scheduler.class);
    final SessionDisableByFinishedJob job = new SessionDisableByFinishedJob(rabbitMQProducer, scheduler);
    final JobExecutionContext context = Mockito.mock(JobExecutionContext.class);

    @Test
    void userEmailVerifiedTask() throws SchedulerException {
        JobDetailImpl jobDetail = Mockito.mock(JobDetailImpl.class);
        Mockito
                .when(context.getJobDetail())
                .thenReturn(jobDetail);
        Mockito
                .when(jobDetail.getName())
                .thenReturn("test");
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.anyString(), Mockito.any(ScheduleType.class));
        Mockito
                .when(jobDetail.getKey())
                .thenReturn(JobKey.jobKey("test"));
        Mockito
                .when(scheduler.deleteJob(Mockito.any(JobKey.class)))
                .thenReturn(true);

        job.execute(context);

        Mockito
                .verify(context, Mockito.times(1))
                .getJobDetail();
        Mockito
                .verify(jobDetail, Mockito.times(1))
                .getName();
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.anyString(), Mockito.any(ScheduleType.class));
        Mockito
                .verify(jobDetail, Mockito.times(1))
                .getKey();
        Mockito
                .verify(scheduler, Mockito.times(1))
                .deleteJob(Mockito.any(JobKey.class));
    }
}
