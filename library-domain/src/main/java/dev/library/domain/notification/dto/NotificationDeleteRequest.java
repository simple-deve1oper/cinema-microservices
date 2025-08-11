package dev.library.domain.notification.dto;

import dev.library.domain.user.dto.UserResponse;

/**
 * DTO для уведомления об удалении бронировании
 * @param bookingId - идентификатор бронирования
 * @param userResponse - объект типа {@link UserResponse}
 */
public record NotificationDeleteRequest(
        Long bookingId,
        UserResponse userResponse
) {
}
