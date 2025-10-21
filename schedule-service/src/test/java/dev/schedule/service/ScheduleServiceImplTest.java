package dev.schedule.service;

import dev.library.domain.schedule.dto.TaskRequest;
import dev.schedule.job.UserDeleteInactiveJob;
import dev.schedule.service.impl.ScheduleServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.*;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class ScheduleServiceImplTest {
    final Scheduler scheduler = Mockito.mock(Scheduler.class);
    final ScheduleService service = new ScheduleServiceImpl(scheduler);

    @Test
    void userEmailVerifiedTask() throws SchedulerException {
        Mockito
                .when(scheduler.scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class)))
                .thenReturn(new Date());

        TaskRequest taskRequest = new TaskRequest("Task Name", 1234);
        service.userEmailVerifiedTask(taskRequest);

        Mockito
                .verify(scheduler, Mockito.times(1))
                .scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class));
    }

    @Test
    void userDeleteInactiveTask() throws SchedulerException {
        JobKey jobKey = new JobKey("Task Name", UserDeleteInactiveJob.class.getName());
        Mockito
                .when(scheduler.getTriggersOfJob(jobKey))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(scheduler.scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class)))
                .thenReturn(new Date());

        TaskRequest taskRequest = new TaskRequest("Task Name", "0 0/5 * * * ?");
        service.userDeleteInactiveTask(taskRequest);

        Mockito
                .verify(scheduler, Mockito.times(1))
                .getTriggersOfJob(jobKey);
        Mockito
                .verify(scheduler, Mockito.times(1))
                .scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class));
    }

    @Test
    void sessionCreateTask() throws SchedulerException {
        Mockito
                .when(scheduler.deleteJob(Mockito.any(JobKey.class)))
                .thenReturn(true);
        Mockito
                .when(scheduler.scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class)))
                .thenReturn(new Date());

        TaskRequest taskRequest = new TaskRequest("Task Name", 1234,
                Map.of("type", "SESSION_START_UPDATE"));
        service.sessionCreateTask(taskRequest);

        Mockito
                .verify(scheduler, Mockito.times(1))
                .deleteJob(Mockito.any(JobKey.class));
        Mockito
                .verify(scheduler, Mockito.times(1))
                .scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class));
    }

    @Test
    void sessionDisableByFinishedTask() throws SchedulerException {
        Mockito
                .when(scheduler.deleteJob(Mockito.any(JobKey.class)))
                .thenReturn(true);
        Mockito
                .when(scheduler.scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class)))
                .thenReturn(new Date());

        TaskRequest taskRequest = new TaskRequest("Task Name", 1234,
                Map.of("type", "SESSION_START_UPDATE"));
        service.sessionCreateTask(taskRequest);

        Mockito
                .verify(scheduler, Mockito.times(1))
                .deleteJob(Mockito.any(JobKey.class));
        Mockito
                .verify(scheduler, Mockito.times(1))
                .scheduleJob(Mockito.any(JobDetail.class), Mockito.any(Trigger.class));
    }

    @Test
    void sessionDeleteTask() throws SchedulerException {
        Mockito
                .when(scheduler.checkExists(Mockito.any(JobKey.class)))
                .thenReturn(true);
        Mockito
                .when(scheduler.deleteJob(Mockito.any(JobKey.class)))
                .thenReturn(true);

        String name = "test";
        service.sessionDeleteTask(name);

        Mockito
                .verify(scheduler, Mockito.times(2))
                .checkExists(Mockito.any(JobKey.class));
        Mockito
                .verify(scheduler, Mockito.times(2))
                .deleteJob(Mockito.any(JobKey.class));
    }
}
