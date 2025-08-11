package dev.library.domain.receipt.dto;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.user.dto.UserResponse;

/**
 * DTO для хранения данных для последующей генерации квитанции
 * @param bookingResponse - объект типа {@link BookingResponse}
 * @param userResponse - объект типа {@link UserResponse}
 */
public record ReceiptRequest(
        BookingResponse bookingResponse,
        UserResponse userResponse
) {}
