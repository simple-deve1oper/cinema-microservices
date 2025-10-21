package dev.schedule.util;

import dev.schedule.job.SessionDisableByFinishedJob;
import dev.schedule.job.UserDeleteInactiveJob;
import dev.schedule.job.UserEmailVerifiedJob;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

public class TaskUtilTest {
    @Test
    void createJobDetail() {
        String name = "test";
        Class<? extends Job> className = SessionDisableByFinishedJob.class;
        JobDetail jobDetail = TaskUtil.createJobDetail(name, className);
        Assertions.assertNotNull(jobDetail);
        Assertions.assertEquals(className, jobDetail.getJobClass());
        JobDetailImpl jobDetailImpl = (JobDetailImpl) jobDetail;
        Assertions.assertEquals(name, jobDetailImpl.getName());
    }

    @Test
    void createTriggerByCount() {
        String name = "test";
        Class<? extends Job> className = UserEmailVerifiedJob.class;
        long milliseconds = 1234;
        Trigger trigger = TaskUtil.createTriggerByCount(name, className, 1234);
        Assertions.assertNotNull(trigger);
        SimpleTriggerImpl simpleTrigger = (SimpleTriggerImpl) trigger;
        Assertions.assertEquals(name, simpleTrigger.getName());
        Assertions.assertEquals(className.getName(), simpleTrigger.getGroup());
        Assertions.assertEquals(milliseconds, simpleTrigger.getStartTime().getTime());
    }

    @Test
    void createTriggerByCron() {
        String cronExpression  = "0/5 * * * * ?";
        String name = "test";
        Class<? extends Job> className = UserDeleteInactiveJob.class;
        Trigger trigger = TaskUtil.createTriggerByCron(name, className, cronExpression);
        Assertions.assertNotNull(trigger);
        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
        Assertions.assertEquals(name, cronTrigger.getName());
        Assertions.assertEquals(className.getName(), cronTrigger.getGroup());
        Assertions.assertEquals(cronExpression, cronTrigger.getCronExpression());
    }
}
