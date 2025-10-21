package dev.booking.repository;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

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

    @Test
    void updateStatus() {
        bookingRepository.updateStatus(Set.of(5L, 6L), BookingStatus.CREATED);
        Booking bookingFive = bookingRepository.findById(5L).orElseThrow();
        Booking bookingSix = bookingRepository.findById(6L).orElseThrow();
        Assertions.assertEquals(BookingStatus.CREATED, bookingFive.getBookingStatus());
        Assertions.assertEquals(BookingStatus.CREATED, bookingSix.getBookingStatus());
    }

    @Test
    void existsBySessionId_true() {
        boolean result = bookingRepository.existsBySessionId(4L);
        Assertions.assertTrue(result);
    }

    @Test
    void existsBySessionId_false() {
        boolean result = bookingRepository.existsBySessionId(999L);
        Assertions.assertFalse(result);
    }
}
