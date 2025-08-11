package dev.booking.mapper;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для преобразования данных типа {@link Booking}
 */
@Component
@RequiredArgsConstructor
public class BookingMapper {
    /**
     * Преобразование данных в {@link BookingResponse}
     * @param booking - объект типа {@link Booking}
     * @param session - объект типа {@link SessionResponse}
     * @param places - список объектов типа {@link PlaceResponse}
     */
    public BookingResponse toResponse(Booking booking, SessionResponse session, List<PlaceResponse> places) {
        return new BookingResponse(
                booking.getId(),
                booking.getUserId(),
                session,
                places,
                booking.getBookingStatus().getValue(),
                booking.getCreatedDate(),
                booking.getUpdatedDate()
        );
    }

    /**
     * Преобразование данных в {@link Booking}
     * @param request - объект типа {@link BookingRequest}
     */
    public Booking toEntity(BookingRequest request) {
        return Booking.builder()
                .userId(request.getUserId())
                .sessionId(request.getSessionId())
                .bookingStatus(request.getBookingStatus())
                .build();
    }
}
