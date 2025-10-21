package dev.booking.service;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.service.impl.TaskServiceImpl;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.schedule.dto.TaskResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {
    final BookingService bookingService = Mockito.mock(BookingService.class);
    final BookingPlaceService bookingPlaceService = Mockito.mock(BookingPlaceService.class);
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final TaskService service = new TaskServiceImpl(bookingService, bookingPlaceService, rabbitMQProducer);

    BookingPlace entityBookingPlaceOne;
    BookingPlace entityBookingPlaceTwo;

    Booking entityBookingOne;

    @BeforeEach
    void init() {
        entityBookingOne = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(1L)
                .bookingStatus(BookingStatus.CREATED)
                .build();

        entityBookingPlaceOne = BookingPlace.builder()
                .id(1L)
                .booking(entityBookingOne)
                .placeId(1L)
                .build();
        entityBookingPlaceTwo = BookingPlace.builder()
                .id(2L)
                .booking(entityBookingOne)
                .placeId(15L)
                .build();
        List<BookingPlace> entitiesBookingPlaces = new ArrayList<>();
        entitiesBookingPlaces.add(entityBookingPlaceOne);
        entitiesBookingPlaces.add(entityBookingPlaceTwo);
        entityBookingOne.setPlaces(entitiesBookingPlaces);
    }

    @Test
    void checkBookingsBySessionId() {
        Mockito
                .when(bookingService.existsBySessionId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(bookingPlaceService.findByBooking_SessionIdAndBooking_BookingStatus(Mockito.anyLong(),
                        Mockito.any(BookingStatus.class)))
                .thenReturn(List.of(entityBookingPlaceOne, entityBookingPlaceTwo));
        Mockito
                .doNothing()
                .when(bookingService)
                .updateStatus(Mockito.anySet(), Mockito.any(BookingStatus.class));
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(TaskResponse.class));

        service.checkBookingsBySessionId("1");

        Mockito.verify(bookingService, Mockito.times(1))
                .existsBySessionId(Mockito.anyLong());
        Mockito.verify(bookingPlaceService, Mockito.times(1))
                .findByBooking_SessionIdAndBooking_BookingStatus(Mockito.anyLong(), Mockito.any(BookingStatus.class));
        Mockito.verify(bookingService, Mockito.times(1))
                .updateStatus(Mockito.anySet(), Mockito.any(BookingStatus.class));
        Mockito.verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(TaskResponse.class));
    }
}
