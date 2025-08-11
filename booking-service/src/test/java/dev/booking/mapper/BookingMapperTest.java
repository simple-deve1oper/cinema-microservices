package dev.booking.mapper;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class BookingMapperTest {
    final BookingMapper mapper = new BookingMapper();

    @Test
    void toResponse() {
        Booking booking = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(1L)
                .bookingStatus(BookingStatus.PAID)
                .build();
        booking.setCreatedDate(OffsetDateTime.now());
        booking.setUpdatedDate(OffsetDateTime.now());
        SessionResponse sessionResponse = new SessionResponse(
                2L,
                125L,
                "3D",
                4,
                OffsetDateTime.now().plusDays(2),
                true
        );
        PlaceResponse placeResponse = new PlaceResponse(
                1L,
                1L,
                1,
                1,
                "350.00",
                false

        );
        BookingResponse response = mapper.toResponse(booking, sessionResponse, List.of(placeResponse));
        Assertions.assertNotNull(response);
        Assertions.assertEquals(booking.getId(), response.id());
        Assertions.assertEquals(booking.getUserId(), response.userId());
        Assertions.assertEquals(sessionResponse, response.session());
        Assertions.assertEquals(1, response.places().size());
        Assertions.assertEquals(placeResponse, response.places().getFirst());
        Assertions.assertEquals(booking.getBookingStatus().getValue(), response.status());
        Assertions.assertEquals(booking.getCreatedDate(), response.createdDate());
        Assertions.assertEquals(booking.getUpdatedDate(), response.updatedDate());
    }

    @Test
    void toEntity() {
        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                1L,
                Set.of(2L),
                BookingStatus.CREATED
        );
        Booking booking = mapper.toEntity(request);
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(request.getUserId(), booking.getUserId());
        Assertions.assertEquals(request.getSessionId(), booking.getSessionId());
        Assertions.assertEquals(request.getBookingStatus(), booking.getBookingStatus());
    }
}
