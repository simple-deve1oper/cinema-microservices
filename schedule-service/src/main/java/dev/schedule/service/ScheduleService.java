package dev.schedule.service;

import dev.library.domain.schedule.dto.TaskRequest;

/**
 * Интерфейс для описания абстрактных методов по созданию и редактированию задач в Quartz
 */
public interface ScheduleService {
    /**
     * Создание задачи о деактивации пользователя
     * @param request - объект типа {@link TaskRequest}
     */
    void userEmailVerifiedTask(TaskRequest request);

    /**
     * Создание задачи об удалении неактивных пользователей
     * @param request - объект типа {@link TaskRequest}
     */
    void userDeleteInactiveTask(TaskRequest request);

    /**
     * Создание задачи о проверке бронирований по идентификатору сеанса
     * @param request - объект типа {@link TaskRequest}
     */
    void sessionCreateTask(TaskRequest request);

    /**
     * Создание задачи о завершении доступности сеанса
     * @param request - объект типа {@link TaskRequest}
     */
    void sessionDisableByFinishedTask(TaskRequest request);

    /**
     * Удаление задач о проверке бронирований по идентификатору сеанса и о завершении доступности сеанса
     * @param name - наименование
     */
    void sessionDeleteTask(String name);
}
