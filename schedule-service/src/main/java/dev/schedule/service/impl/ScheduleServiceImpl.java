package dev.schedule.service.impl;

import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.schedule.dto.TaskRequest;
import dev.schedule.job.BookingCheckBeforeStartSessionJob;
import dev.schedule.job.SessionDisableByFinishedJob;
import dev.schedule.job.UserDeleteInactiveJob;
import dev.schedule.job.UserEmailVerifiedJob;
import dev.schedule.service.ScheduleService;
import dev.schedule.util.TaskUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис, реализующий интерфейс {@link ScheduleService}
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {
    private final Scheduler scheduler;

    private final static String ERROR_RUN_TASK = "Ошибка при запуске задачи";

    @Override
    @RabbitListener(queues = {"${rabbitmq.task.queue.user.email-verified}"})
    public void userEmailVerifiedTask(TaskRequest request) {
        log.debug("Started userEmailVerifiedTask(TaskRequest request) with request = {}", request);
        try {
            JobDetail jobDetail = TaskUtil.createJobDetail(request.name(), UserEmailVerifiedJob.class);
            Trigger trigger = TaskUtil.createTriggerByCount(request.name(), UserEmailVerifiedJob.class, request.millisecondsToStart());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(ERROR_RUN_TASK.concat(" в методе userEmailVerifiedTask(TaskRequest request): %s".formatted(e)));
            throw new IllegalStateException(e);
        }
        log.debug("Finished userEmailVerifiedTask(TaskRequest request) with request = {}", request);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.task.queue.user.delete-inactive}"})
    public void userDeleteInactiveTask(TaskRequest request) {
        log.debug("Started userDeleteInactiveTask(TaskRequest request) with request = {}", request);
        try {
            JobKey jobKey = new JobKey(request.name(), UserDeleteInactiveJob.class.getName());
            List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
            if (triggers.isEmpty()) {
                JobDetail jobDetail = TaskUtil.createJobDetail(request.name(), UserDeleteInactiveJob.class);
                Trigger trigger = TaskUtil.createTriggerByCron(request.name(), UserDeleteInactiveJob.class, request.cron());
                scheduler.scheduleJob(jobDetail, trigger);
            }
        } catch (SchedulerException e) {
            log.error(ERROR_RUN_TASK.concat(" в методе userDeleteInactiveTask(TaskRequest request): %s".formatted(e)));
            throw new IllegalStateException(e);
        }
        log.debug("Finished userDeleteInactiveTask(TaskRequest request) with request = {}", request);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.task.queue.session.before-start}"})
    public void sessionCreateTask(TaskRequest request) {
        log.debug("Started sessionCreateTask(TaskRequest request) with request = {}", request);
        try {
            String type = (String) request.additionalProperties().get("type");
            ActionType actionType = ActionType.valueOf(type);
            if (actionType == ActionType.SESSION_START_UPDATE) {
                JobKey jobKey = new JobKey(request.name(), BookingCheckBeforeStartSessionJob.class.getName());
                scheduler.deleteJob(jobKey);
                log.debug("Job deleted by jobKey = {} in method sessionCreateTask(TaskRequest request) because actionType equal to SESSION_START_UPDATE", jobKey);
            }
            JobDetail jobDetail = TaskUtil.createJobDetail(request.name(), BookingCheckBeforeStartSessionJob.class);
            Trigger trigger = TaskUtil.createTriggerByCount(request.name(), BookingCheckBeforeStartSessionJob.class, request.millisecondsToStart());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(ERROR_RUN_TASK.concat(" в методе sessionCreateTask(TaskRequest request): %s".formatted(e)));
            throw new IllegalStateException(e);
        }
        log.debug("Finished sessionCreateTask(TaskRequest request) with request = {}", request);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.task.queue.session.disable-by-finished}"})
    public void sessionDisableByFinishedTask(TaskRequest request) {
        log.debug("Started sessionDisableByFinishedTask(TaskRequest request) with request = {}", request);
        try {
            String type = (String) request.additionalProperties().get("type");
            ActionType actionType = ActionType.valueOf(type);
            if (actionType == ActionType.SESSION_START_UPDATE) {
                JobKey jobKey = new JobKey(request.name(), SessionDisableByFinishedJob.class.getName());
                scheduler.deleteJob(jobKey);
                log.debug("Job deleted by jobKey = {} in method sessionDisableByFinishedTask(TaskRequest request) because actionType equal to SESSION_START_UPDATE", jobKey);
            }
            JobDetail jobDetail = TaskUtil.createJobDetail(request.name(), SessionDisableByFinishedJob.class);
            Trigger trigger = TaskUtil.createTriggerByCount(request.name(), SessionDisableByFinishedJob.class, request.millisecondsToStart());
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error(ERROR_RUN_TASK.concat(" в методе sessionDisableByFinishedTask(TaskRequest request): %s".formatted(e)));
            throw new IllegalStateException(e);
        }
        log.debug("Finished sessionDisableByFinishedTask(TaskRequest request) with request = {}", request);
    }

    @Override
    @RabbitListener(queues = {"${rabbitmq.task.queue.session.delete}"})
    public void sessionDeleteTask(String name) {
        log.debug("Started sessionDeleteTask(String name) with name = {}", name);
        try {
            JobKey jobKeyBookingCheckBeforeStartSessionJob = new JobKey(name, BookingCheckBeforeStartSessionJob.class.getName());
            if (scheduler.checkExists(jobKeyBookingCheckBeforeStartSessionJob)) {
                scheduler.deleteJob(jobKeyBookingCheckBeforeStartSessionJob);
                log.debug("Job deleted by jobKey = {}", jobKeyBookingCheckBeforeStartSessionJob);
            }
            JobKey jobKeySessionDisableByFinishedJob = new JobKey(name, SessionDisableByFinishedJob.class.getName());
            if (scheduler.checkExists(jobKeySessionDisableByFinishedJob)) {
                scheduler.deleteJob(jobKeySessionDisableByFinishedJob);
                log.debug("Job deleted by jobKey = {}", jobKeySessionDisableByFinishedJob);
            }
        } catch (SchedulerException e) {
            log.error(ERROR_RUN_TASK.concat(" в методе sessionDeleteTask(TaskRequest request): %s".formatted(e)));
            throw new IllegalStateException(e);
        }
        log.debug("Finished sessionDeleteTask(String name) with name = {}", name);
    }
}
