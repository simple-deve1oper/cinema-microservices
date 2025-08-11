package dev.receipt.service.impl;

import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.booking.client.BookingClient;
import dev.receipt.service.BookingCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Сервис, реализующий интерфейс {@link BookingCheckService}
 */
@Service
@RequiredArgsConstructor
public class BookingCheckServiceImpl implements BookingCheckService {
    private final BookingClient bookingClient;

    @Value("${errors.booking.id-and-user-id.not-found}")
    private String errorReceiptBookingIdAndUserIdNotFound;

    @Override
    public void checkExistsByBookingIdAndUserId(Long bookingId, String userId) {
        if (!bookingClient.existsByIdAndUserId(bookingId, userId)) {
            throw new EntityNotFoundException(errorReceiptBookingIdAndUserIdNotFound.formatted(bookingId, userId));
        }
    }
}
