package dev.library.domain.receipt.dto;

import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для хранения данных для последующей генерации квитанции
 * @param bookingResponse - объект типа {@link BookingResponse}
 * @param userResponse - объект типа {@link UserResponse}
 */
public record ReceiptRequest(
        @Schema(name = "bookingResponse", description = "Бронирование")
        BookingResponse bookingResponse,
        @Schema(name = "userResponse", description = "Пользователь")
        UserResponse userResponse
) {}
