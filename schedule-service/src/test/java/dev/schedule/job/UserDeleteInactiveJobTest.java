package dev.schedule.job;

import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.library.domain.schedule.dto.TaskResponse;
import dev.schedule.service.RabbitMQProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;

@ExtendWith(MockitoExtension.class)
public class UserDeleteInactiveJobTest {
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final UserDeleteInactiveJob job = new UserDeleteInactiveJob(rabbitMQProducer);
    final JobExecutionContext context = Mockito.mock(JobExecutionContext.class);

    @Test
    void userEmailVerifiedTask() {
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(TaskResponse.class), Mockito.any(ScheduleType.class));

        job.execute(context);

        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(TaskResponse.class), Mockito.any(ScheduleType.class));
    }
}
