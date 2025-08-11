package dev.booking.mapper;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.library.domain.booking.dto.constant.BookingStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

public class BookingPlaceMapperTest {
    final BookingPlaceMapper mapper = new BookingPlaceMapper();

    @Test
    void toEntity() {
        Booking booking = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(1L)
                .bookingStatus(BookingStatus.PAID)
                .build();
        booking.setCreatedDate(OffsetDateTime.now());
        booking.setUpdatedDate(OffsetDateTime.now());

        BookingPlace entity = mapper.toEntity(booking, 3L);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(booking, entity.getBooking());
        Assertions.assertEquals(3L, entity.getPlaceId());
    }
}
