package dev.receipt.service;

import dev.receipt.entity.Receipt;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.receipt.dto.ReceiptRequest;
import org.springframework.core.io.Resource;

/**
 * Репозиторий для сущности {@link Receipt}
 */
public interface ReceiptService {
    /**
     * Получение файла квитанции по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    Resource getByBookingId(Long bookingId);

    /**
     * Создание новой записи о квитанции
     * @param request - объект типа {@link ReceiptRequest}
     */
    void create(ReceiptRequest request);

    /**
     * Обновление существующей записи о квитанции
     * @param request - объект типа {@link ReceiptRequest}
     */
    void update(ReceiptRequest request);

    void updateStatus(ReceiptRequest request);

    /**
     * Удаление записи по идентификатору бронирования
     * @param notificationDeleteRequest - объект типа {@link NotificationDeleteRequest}
     */
    void deleteByBookingId(NotificationDeleteRequest notificationDeleteRequest);
}
