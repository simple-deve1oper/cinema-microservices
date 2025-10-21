package dev.booking.service;

/**
 * Сервис для описания абстрактных методов выполнения фоновых задач для бронирований
 */
public interface TaskService {
    /**
     * Проверка бронирований по идентификатору сеансов на то, что существуют записи со статусом CREATED
     * @param sessionId - идентификатор сеансов
     */
    void checkBookingsBySessionId(String sessionId);
}
