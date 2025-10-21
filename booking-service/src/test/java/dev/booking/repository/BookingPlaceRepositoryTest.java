package dev.booking.repository;

import dev.booking.entity.BookingPlace;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public class BookingPlaceRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private BookingPlaceRepository bookingPlaceRepository;

    @Test
    void findByBooking_SessionIdAndBooking_BookingStatus() {
        List<BookingPlace> bookingPlaces = bookingPlaceRepository.findByBooking_SessionIdAndBooking_BookingStatus(4L, BookingStatus.PAID);
        Assertions.assertNotNull(bookingPlaces);
        Assertions.assertFalse(bookingPlaces.isEmpty());
        Assertions.assertEquals(3, bookingPlaces.size());
    }
}
