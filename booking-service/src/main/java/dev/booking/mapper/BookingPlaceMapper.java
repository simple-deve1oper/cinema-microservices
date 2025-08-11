package dev.booking.mapper;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link BookingPlace}
 */
@Component
public class BookingPlaceMapper {
    /**
     * Преобразование данных в {@link BookingPlace}
     * @param booking - объект типа {@link Booking}
     * @param placeId - идентификатор места
     */
    public BookingPlace toEntity(Booking booking, Long placeId) {
        return BookingPlace.builder()
                .booking(booking)
                .placeId(placeId)
                .build();
    }
}
