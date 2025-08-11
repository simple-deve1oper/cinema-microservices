package dev.booking.repository;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
public class BookingRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void findByIdAndUserId_ok() {
        Optional<Booking> optionalBooking = bookingRepository.findByIdAndUserId(2L, "14b8135e-4a62-4104-ac6a-26eefaeeef17");
        Assertions.assertTrue(optionalBooking.isPresent());

        Booking booking = optionalBooking.get();
        Assertions.assertEquals(2L, booking.getId());
        Assertions.assertEquals("14b8135e-4a62-4104-ac6a-26eefaeeef17", booking.getUserId());
        Assertions.assertEquals(4L, booking.getSessionId());
        Assertions.assertEquals(BookingStatus.PAID, booking.getBookingStatus());


    }

    @Test
    void findByIdAndUserId_empty() {
        Optional<Booking> optionalBooking = bookingRepository.findByIdAndUserId(999L, "14b8135e-4a62-4104-ac6a-26eefaeeef17");
        Assertions.assertTrue(optionalBooking.isEmpty());
    }

    @Test
    void existsByIdAndBookingStatus() {
        boolean result = bookingRepository.existsByIdAndBookingStatus(2L, BookingStatus.PAID);
        Assertions.assertTrue(result);

        result = bookingRepository.existsByIdAndBookingStatus(3L, BookingStatus.CANCELED);
        Assertions.assertFalse(result);
    }
}
