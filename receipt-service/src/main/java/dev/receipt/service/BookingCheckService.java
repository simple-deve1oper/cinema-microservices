package dev.receipt.service;

/**
 * Интерфейс для описания абстрактных методов по проверке бронирования пользователя
 */
public interface BookingCheckService {
    /**
     * Проверка существования бронирования у пользователя
     * @param bookingId - идентификатор бронирования
     * @param userId - идентификатор пользователя
     */
    void checkExistsByBookingIdAndUserId(Long bookingId, String userId);
}
