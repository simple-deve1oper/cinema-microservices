package dev.session.service;

import dev.library.domain.schedule.dto.TaskResponse;

/**
 * Сервис для описания абстрактных методов выполнения фоновых задач для сеансов
 */
public interface TaskService {
    /**
     * Отключение доступности сеансов для новых бронирований
     * @param sessionId - идентификатор сеанса
     */
    void disableByFinishedSession(String sessionId);

    /**
     * Обновление доступности места после проверки бронирований по идентификатору сенса
     * @param response - объект типа {@link TaskResponse}
     */
    void updateAvailablePlacesAfterCheckBookingsBySession(TaskResponse response);
}
