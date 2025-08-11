package dev.booking.service;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.mapper.BookingPlaceMapper;
import dev.booking.repository.BookingPlaceRepository;
import dev.booking.service.impl.BookingPlaceServiceImpl;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.session.client.PlaceClient;
import dev.library.domain.session.dto.PlaceResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class BookingPlaceServiceImplTest {
    final BookingPlaceRepository repository = Mockito.mock(BookingPlaceRepository.class);
    final BookingPlaceMapper mapper = new BookingPlaceMapper();
    final PlaceClient placeClient = Mockito.mock(PlaceClient.class);
    final BookingPlaceService service = new BookingPlaceServiceImpl(repository, mapper, placeClient);

    BookingPlace entityBookingPlaceOne;
    BookingPlace entityBookingPlaceTwo;

    Booking entityBookingOne;

    PlaceResponse placeResponseOne;
    PlaceResponse placeResponseTwo;
    PlaceResponse placeResponseThree;

    @BeforeEach
    void init() {
        entityBookingOne = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(1L)
                .bookingStatus(BookingStatus.PAID)
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

        placeResponseOne = new PlaceResponse(
                1L,
                1L,
                1,
                1,
                "300.00",
                true
        );
        placeResponseTwo = new PlaceResponse(
                15L,
                1L,
                3,
                15,
                "400.00",
                true
        );
        placeResponseThree = new PlaceResponse(
                45L,
                6L,
                2,
                8,
                "400.00",
                false
        );
    }

    @Test
    void getPlaceResponses_ok() {
        Mockito
                .when(placeClient.getAllByIds(Mockito.anySet()))
                .thenReturn(List.of(placeResponseOne, placeResponseTwo));

        List<PlaceResponse> responses = service.getPlaceResponses(Set.of(1L, 15L));
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getAllByIds(Mockito.anySet());
    }

    @Test
    void getPlaceResponses_some() {
        Mockito
                .when(placeClient.getAllByIds(Mockito.anySet()))
                .thenReturn(List.of(placeResponseTwo));

        List<PlaceResponse> responses = service.getPlaceResponses(Set.of(15L, 999L));
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(1, responses.size());

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getAllByIds(Mockito.anySet());
    }

    @Test
    void getPlaceResponses_empty() {
        Mockito
                .when(placeClient.getAllByIds(Mockito.anySet()))
                .thenReturn(Collections.emptyList());

        List<PlaceResponse> responses = service.getPlaceResponses(Set.of(99998L, 99999L));
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getAllByIds(Mockito.anySet());
    }

    @Test
    void getPlaceBySessionIdAndAndIdsAndAvailableFalse_ok() {
        Mockito
                .when(placeClient.getPlaceBySessionIdAndIdsAndAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean()))
                .thenReturn(112L);

        long id = service.getPlaceBySessionIdAndIdsAndAvailableFalse(5L, Set.of(110L, 111L, 112L));
        Assertions.assertEquals(112, id);

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getPlaceBySessionIdAndIdsAndAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void getPlaceBySessionIdAndAndIdsAndAvailableFalse_zero() {
        Mockito
                .when(placeClient.getPlaceBySessionIdAndIdsAndAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean()))
                .thenReturn(0L);

        long id = service.getPlaceBySessionIdAndIdsAndAvailableFalse(56778L, Set.of(1109L, 11188L, 11275L));
        Assertions.assertEquals(0, id);

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getPlaceBySessionIdAndIdsAndAvailable(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void getPlaceNotEqualsSessionBySessionIdAndIds_ok() {
        Mockito
                .when(placeClient.getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(1246L);

        long id = service.getPlaceNotEqualsSessionBySessionIdAndIds(144L, Set.of(1006L, 1116L, 1246L));
        Assertions.assertEquals(1246, id);

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
    }

    @Test
    void getPlaceNotEqualsSessionBySessionIdAndIds_zero() {
        Mockito
                .when(placeClient.getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(0L);

        long id = service.getPlaceNotEqualsSessionBySessionIdAndIds(139L, Set.of(996L, 997L, 998L));
        Assertions.assertEquals(0, id);

        Mockito
                .verify(placeClient, Mockito.times(1))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
    }

    @Test
    void create() {
        Mockito
                .doNothing()
                .when(placeClient)
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
        Mockito
                .when(repository.saveAll(Mockito.anyIterable()))
                .thenReturn(List.of(entityBookingPlaceOne, entityBookingPlaceTwo));

        List<BookingPlace> bookingPlaces = service.create(1L, entityBookingOne, Set.of(1L, 15L));
        Assertions.assertNotNull(bookingPlaces);
        Assertions.assertFalse(bookingPlaces.isEmpty());
        Assertions.assertEquals(2, bookingPlaces.size());
        Assertions.assertEquals(1, bookingPlaces.get(0).getPlaceId());
        Assertions.assertEquals(15, bookingPlaces.get(1).getPlaceId());

        Mockito
                .verify(placeClient, Mockito.times(1))
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyIterable());
    }

    @Test
    void update() {
        Mockito
                .doNothing()
                .when(placeClient)
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());

        service.update(77L, entityBookingOne, Set.of(1L), Set.of(16L));

        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyIterable());
        Mockito
                .verify(placeClient, Mockito.times(3))
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void updateAvailability() {
        Mockito
                .doNothing()
                .when(placeClient)
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());

        service.updateAvailability(12L, Set.of(77L, 78L), true);

        Mockito
                .verify(placeClient, Mockito.times(1))
                .updateAvailabilityAtPlaces(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void getIdsForRemove() {
        Set<Long> currentPlaceIds = new HashSet<>();
        currentPlaceIds.add(1L);
        currentPlaceIds.add(2L);
        currentPlaceIds.add(3L);
        Set<Long> placeIds = new HashSet<>();
        placeIds.add(1L);
        placeIds.add(3L);
        Set<Long> ids = service.getIdsForRemove(currentPlaceIds, placeIds);
        Assertions.assertNotNull(ids);
        Assertions.assertFalse(ids.isEmpty());
        Assertions.assertEquals(1, ids.size());
        Assertions.assertEquals(2L, ids.iterator().next());
    }

    @Test
    void getIdsForCreate() {
        Set<Long> currentPlaceIds = new HashSet<>();
        currentPlaceIds.add(1L);
        currentPlaceIds.add(2L);
        currentPlaceIds.add(3L);
        Set<Long> placeIds = new HashSet<>();
        placeIds.add(4L);
        Set<Long> ids = service.getIdsForCreate(currentPlaceIds, placeIds);
        Assertions.assertNotNull(ids);
        Assertions.assertFalse(ids.isEmpty());
        Assertions.assertEquals(1, ids.size());
        Assertions.assertEquals(4L, ids.iterator().next());
    }
}
