package dev.library.domain.notification.dto;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.user.dto.UserResponse;

/**
 * DTO для уведомления о бронировании
 * @param bookingResponse - объект типа {@link BookingResponse}
 * @param userResponse - объект типа {@link UserResponse}
 * @param data - файл в байтовом представлении
 */
public record NotificationRequest(
        BookingResponse bookingResponse,
        UserResponse userResponse,
        byte[] data
) {}
