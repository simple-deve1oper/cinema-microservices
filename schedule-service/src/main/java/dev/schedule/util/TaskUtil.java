package dev.schedule.util;

import org.quartz.*;

import java.util.Date;

/**
 * Вспомогательный класс для работы с объектами Quartz
 */
public class TaskUtil {
    /**
     * Создание объекта типа {@link JobDetail}
     * @param name - наименование
     * @param className - тип класса
     */
    public static JobDetail createJobDetail(String name, Class<? extends Job> className) {
        return JobBuilder.newJob(className)
                .withIdentity(name, className.getName())
                .storeDurably()
                .requestRecovery()
                .build();
    }

    /**
     * Создание объекта типа {@link Trigger}, с помощью которого задача выполняется только один раз
     * @param name - наименование
     * @param className - тип класса
     * @param milliseconds - время начала выполнения задачи в миллисекундах
     */
    public static Trigger createTriggerByCount(String name, Class<? extends Job> className, long milliseconds) {
        return TriggerBuilder.newTrigger()
                .withIdentity(name, className.getName())
                .startAt(new Date(milliseconds))
                .build();
    }

    /**
     * Создание объекта типа {@link Trigger}, с помощью которого задача выполняется по cron-выражению
     * @param name - наименование
     * @param className - тип класса
     * @param cronExpression - cron-выражение
     */
    public static Trigger createTriggerByCron(String name, Class<? extends Job> className, String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(name, className.getName())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }
}
