package dev.notification.service;

import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;

/**
 * Интерфейс для описания абстрактных методов по работе с уведомлениями
 */
public interface NotificationService {
    void create(NotificationRequest notificationRequest);
    void update(NotificationRequest notificationRequest);
    void updateStatus(NotificationRequest notificationRequest);
    void delete(NotificationDeleteRequest notificationDeleteRequest);
}
