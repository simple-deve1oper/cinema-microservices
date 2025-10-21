package dev.library.domain.notification.dto;

import dev.library.domain.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для уведомления об удалении бронировании
 * @param bookingId - идентификатор бронирования
 * @param userResponse - объект типа {@link UserResponse}
 */
public record NotificationDeleteRequest(
        @Schema(name = "bookingId", description = "Идентификатор бронирования")
        Long bookingId,
        @Schema(name = "userResponse", description = "Пользователь")
        UserResponse userResponse
) {
}
