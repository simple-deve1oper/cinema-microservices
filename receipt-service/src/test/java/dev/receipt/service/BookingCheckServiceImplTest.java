package dev.receipt.service;

import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.booking.client.BookingClient;
import dev.receipt.service.impl.BookingCheckServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class BookingCheckServiceImplTest {
    final BookingClient bookingClient = Mockito.mock(BookingClient.class);
    final BookingCheckService service = new BookingCheckServiceImpl(bookingClient);

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorReceiptBookingIdAndUserIdNotFound", "Бронирование с идентификатором %d для пользователя %s не найдено");
    }

    @Test
    void checkExistsByBookingIdAndUserId_ok() {
        Mockito
                .when(bookingClient.existsByIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(true);

        service.checkExistsByBookingIdAndUserId(1L, UUID.randomUUID().toString());

        Mockito
                .verify(bookingClient, Mockito.times(1))
                .existsByIdAndUserId(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void checkExistsByBookingIdAndUserId_accessForbiddenException() {
        Mockito
                .when(bookingClient.existsByIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(false);

        String userId = UUID.randomUUID().toString();
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.checkExistsByBookingIdAndUserId(9999L, userId)
                );
        var expectedMessage = "Бронирование с идентификатором 9999 для пользователя %s не найдено".formatted(userId);
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(bookingClient, Mockito.times(1))
                .existsByIdAndUserId(Mockito.anyLong(), Mockito.anyString());
    }
}
