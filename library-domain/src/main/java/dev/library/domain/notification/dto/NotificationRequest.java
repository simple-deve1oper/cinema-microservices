package dev.library.domain.notification.dto;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для уведомления о бронировании
 * @param bookingResponse - объект типа {@link BookingResponse}
 * @param userResponse - объект типа {@link UserResponse}
 * @param data - файл в байтовом представлении
 */
public record NotificationRequest(
        @Schema(name = "bookingResponse", description = "Бронирование")
        BookingResponse bookingResponse,
        @Schema(name = "userResponse", description = "Пользователь")
        UserResponse userResponse,
        @Schema(name = "data", description = "Файл в байтовом представлении")
        byte[] data
) {}
