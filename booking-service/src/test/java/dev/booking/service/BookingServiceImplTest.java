package dev.booking.service;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.booking.mapper.BookingMapper;
import dev.booking.repository.BookingRepository;
import dev.booking.service.impl.BookingServiceImpl;
import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.core.util.DateUtil;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.BookingSearchRequest;
import dev.library.domain.booking.dto.BookingStatusRequest;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.rabbitmq.ActionType;
import dev.library.domain.receipt.dto.ReceiptRequest;
import dev.library.domain.session.client.SessionClient;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.client.UserClient;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    final BookingRepository repository = Mockito.mock(BookingRepository.class);
    final BookingMapper mapper = new BookingMapper();
    final BookingPlaceService bookingPlaceService = Mockito.mock(BookingPlaceService.class);
    final SessionClient sessionClient = Mockito.mock(SessionClient.class);
    final MovieClient movieClient = Mockito.mock(MovieClient.class);
    final UserClient userClient = Mockito.mock(UserClient.class);
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final SpecificationBuilder<Booking> specificationBuilder = new SpecificationBuilder<>();
    final BookingService service = new BookingServiceImpl(repository, mapper, bookingPlaceService, sessionClient,
            movieClient, userClient, rabbitMQProducer, specificationBuilder);

    BookingPlace entityBookingPlaceOne;
    BookingPlace entityBookingPlaceTwo;
    BookingPlace entityBookingPlaceThree;

    Booking entityBookingOne;
    Booking entityBookingTwo;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorBookingIdNotFound", "Бронирование с идентификатором %d не найдено");
        ReflectionTestUtils.setField(service, "errorBookingIdAndUserIdNotFound", "Бронирование с идентификатором %d для пользователя %s не найдено");
        ReflectionTestUtils.setField(service, "errorBookingIdAndBookingStatusAlreadyExists", "Статус бронирования %s у брони с идентификатором %d уже существует");
        ReflectionTestUtils.setField(service, "errorBookingIdAndBookingStatusBadRequest", "Невозможно обновить отмененную бронь");
        ReflectionTestUtils.setField(service, "errorBookingStatusBadRequest", "Невозможно создать отмененную бронь");
        ReflectionTestUtils.setField(service, "errorBookingPlacesAlreadyExists", "Место с идентификатором %d занято");
        ReflectionTestUtils.setField(service, "errorBookingPlacesBadRequest", "Место с идентификатором %d не относится к сеансу с идентификатором %d");
        ReflectionTestUtils.setField(service, "errorBookingSessionTimeEndBadRequest", "Бронирование мест невозможно, т.к. сеанс %s в зале %d закончен");
        ReflectionTestUtils.setField(service, "errorBookingSessionAvailableBadRequest", "Сеанс с идентификатором %d недоступен для бронирования");

        OffsetDateTime dateTime = OffsetDateTime.now();
        entityBookingOne = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(1L)
                .bookingStatus(BookingStatus.PAID)
                .build();
        entityBookingOne.setCreatedDate(dateTime.minusDays(3));
        entityBookingOne.setUpdatedDate(dateTime.minusDays(3));
        entityBookingTwo = Booking.builder()
                .id(2L)
                .userId("1241754e-1dcb-4273-bc84-433f084919e0")
                .sessionId(2L)
                .bookingStatus(BookingStatus.CANCELED)
                .build();
        entityBookingTwo.setCreatedDate(dateTime);
        entityBookingTwo.setUpdatedDate(dateTime);

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
        entityBookingPlaceThree = BookingPlace.builder()
                .id(3L)
                .booking(entityBookingTwo)
                .placeId(31L)
                .build();

        entityBookingOne.setPlaces(List.of(entityBookingPlaceOne, entityBookingPlaceTwo));
        entityBookingTwo.setPlaces(List.of(entityBookingPlaceThree));
    }

    @Test
    void getAll_ok() {
        List<Booking> entities = List.of(entityBookingTwo, entityBookingOne);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Booking>>any(), Mockito.any(Sort.class)))
                .thenReturn(entities);

        List<Booking> bookings = service.getAll(new BookingSearchRequest());
        Assertions.assertNotNull(bookings);
        Assertions.assertFalse(bookings.isEmpty());
        Assertions.assertEquals(2, bookings.size());
        Assertions.assertEquals(2L, bookings.get(0).getId());
        Assertions.assertEquals(1L, bookings.get(1).getId());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Booking>>any(), Mockito.any(Sort.class));
    }

    @Test
    void getAll_some() {
        List<Booking> entities = List.of(entityBookingOne);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Booking>>any(), Mockito.any(Sort.class)))
                .thenReturn(entities);

        List<Booking> bookings = service.getAll(
                new BookingSearchRequest(
                        "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                        1L,
                        BookingStatus.PAID,
                        LocalDate.now().minusDays(3),
                        LocalDate.now().minusDays(3)
                )
        );
        Assertions.assertNotNull(bookings);
        Assertions.assertFalse(bookings.isEmpty());
        Assertions.assertEquals(1, bookings.size());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", bookings.getFirst().getUserId());
        Assertions.assertEquals(1L, bookings.getFirst().getSessionId());
        Assertions.assertEquals(BookingStatus.PAID, bookings.getFirst().getBookingStatus());
        Assertions.assertEquals(LocalDate.now().minusDays(3), bookings.getFirst().getCreatedDate().toLocalDate());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Booking>>any(), Mockito.any(Sort.class));
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(Collections.emptyList());

        List<Booking> bookings = service.getAll(
                new BookingSearchRequest(
                        "b9aa06e9-3523-491b-a87c-fbcb4bc92bd7",
                        158L,
                        BookingStatus.PAID,
                        LocalDate.now(),
                        LocalDate.now()
                )
        );
        Assertions.assertNotNull(bookings);
        Assertions.assertTrue(bookings.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Booking>>any(), Mockito.any(Sort.class));
    }

    @Test
    void getById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingTwo));

        Booking booking = service.getById(2L);
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(2L, booking.getId());
        Assertions.assertEquals("1241754e-1dcb-4273-bc84-433f084919e0", booking.getUserId());
        Assertions.assertEquals(2L, booking.getSessionId());
        Assertions.assertEquals(BookingStatus.CANCELED, booking.getBookingStatus());
        Assertions.assertEquals(1, booking.getPlaces().size());
        Assertions.assertEquals(3L, booking.getPlaces().getFirst().getId());
        Assertions.assertEquals(31L, booking.getPlaces().getFirst().getPlaceId());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getById_entityNotFoundException() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getById(123456L)
                );
        var expectedMessage = "Бронирование с идентификатором 123456 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getByIdAndUser_ok() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.findByIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Optional.of(entityBookingOne));

        Booking booking = service.getById(1L, "53abe284-8b21-4a44-97a6-2df9f84f6aac");
        Assertions.assertNotNull(booking);
        Assertions.assertEquals(1L, booking.getId());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", booking.getUserId());
        Assertions.assertEquals(1L, booking.getSessionId());
        Assertions.assertEquals(BookingStatus.PAID, booking.getBookingStatus());
        Assertions.assertEquals(2, booking.getPlaces().size());
        Assertions.assertEquals(1L, booking.getPlaces().get(0).getId());
        Assertions.assertEquals(1L, booking.getPlaces().get(0).getPlaceId());
        Assertions.assertEquals(2L, booking.getPlaces().get(1).getId());
        Assertions.assertEquals(15L, booking.getPlaces().get(1).getPlaceId());

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .findByIdAndUserId(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getByIdAndUser_entityNotFoundException_booking() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getById(123456L, "53abe284-8b21-4a44-97a6-2df9f84f6aac")
                );
        var expectedMessage = "Бронирование с идентификатором 123456 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .findByIdAndUserId(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getByIdAndUser_entityNotFoundException() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.findByIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getById(35L, "53abe284-8b21-4a44-97a6-2df9f84f6aac")
                );
        var expectedMessage = "Бронирование с идентификатором 35 для пользователя 53abe284-8b21-4a44-97a6-2df9f84f6aac не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .findByIdAndUserId(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void existsByUserId_true() {
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(true);

        boolean result = service.existsByUserId(1L, "53abe284-8b21-4a44-97a6-2df9f84f6aac");
        Assertions.assertTrue(result);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
    }

    @Test
    void existsByUserId_false() {
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);

        boolean result = service.existsByUserId(9987L, "53abe284-8b21-4a44-97a6-2df9f84f6aac");
        Assertions.assertFalse(result);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
    }

    @Test
    void create_ok() {
        Booking entityBooking = Booking.builder()
                .id(7L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(2L)
                .bookingStatus(BookingStatus.CREATED)
                .build();
        entityBooking.setCreatedDate(OffsetDateTime.now());
        entityBooking.setUpdatedDate(OffsetDateTime.now());
        BookingPlace entityBookingPlace = BookingPlace.builder()
                .id(19L)
                .booking(entityBooking)
                .placeId(55L)
                .build();
        entityBooking.setPlaces(List.of(entityBookingPlace));
        SessionResponse sessionResponse = new SessionResponse(
                2L,
                125L,
                "3D",
                4,
                OffsetDateTime.now().plusDays(2),
                true
        );
        PlaceResponse placeResponse = new PlaceResponse(
                55L,
                2L,
                15,
                3,
                "400.00",
                false
        );

        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(75);
        Mockito
                .when(bookingPlaceService.getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(0L);
        Mockito
                .when(bookingPlaceService.getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(0L);
        Mockito
                .when(repository.save(Mockito.any(Booking.class)))
                .thenReturn(entityBooking);
        Mockito
                .when(bookingPlaceService.create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet()))
                .thenReturn(List.of(entityBookingPlace));
        Mockito
                .when(bookingPlaceService.getPlaceResponses(Mockito.anySet()))
                .thenReturn(List.of(placeResponse));

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                2L,
                Set.of(55L),
                BookingStatus.CREATED
        );
        BookingResponse response = service.create(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(7L, response.id());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", response.userId());
        Assertions.assertEquals(2L, response.session().id());
        Assertions.assertEquals(BookingStatus.CREATED.getValue(), response.status());
        Assertions.assertEquals(1, response.places().size());
        Assertions.assertEquals(55L, response.places().getFirst().id());

        Mockito
                .verify(sessionClient, Mockito.times(2))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceResponses(Mockito.anySet());
    }

    @Test
    void create_badRequestException_statusCancelled() {
        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                2L,
                Set.of(55L),
                BookingStatus.CANCELED
        );

        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Невозможно создать отмененную бронь";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(sessionClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
    }

    @Test
    void create_badRequestException_session_notAvailable() {
        SessionResponse sessionResponse = new SessionResponse(
                5L,
                99L,
                "2D",
                1,
                OffsetDateTime.now().plusDays(1),
                false
        );

        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                5L,
                Set.of(77L),
                BookingStatus.CREATED
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Сеанс с идентификатором 5 недоступен для бронирования";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
    }

    @Test
    void create_badRequestException_session_startTime() {
        OffsetDateTime dateTime = OffsetDateTime.now().minusHours(1);
        SessionResponse sessionResponse = new SessionResponse(
                10L,
                100L,
                "2D",
                3,
                dateTime,
                true
        );

        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(36);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                10L,
                Set.of(104L),
                BookingStatus.PAID
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Бронирование мест невозможно, т.к. сеанс %s в зале %d закончен"
                .formatted(DateUtil.formatDate(sessionResponse.dateTime()), sessionResponse.hall());
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
    }

    @Test
    void create_entityAlreadyExistsException_places_notEqualsSessionBySessionIdAndIds() {
        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(1);
        SessionResponse sessionResponse = new SessionResponse(
                56L,
                100L,
                "2D",
                3,
                dateTime,
                true
        );

        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(36);
        Mockito
                .when(bookingPlaceService.getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(16L);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                56L,
                Set.of(12L,16L),
                BookingStatus.PAID
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Место с идентификатором 16 не относится к сеансу с идентификатором 56";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
    }

    @Test
    void create_entityAlreadyExistsException_places_bySessionIdAndIdsAndAvailableFalse() {
        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(1);
        SessionResponse sessionResponse = new SessionResponse(
                56L,
                100L,
                "2D",
                3,
                dateTime,
                true
        );

        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(36);
        Mockito
                .when(bookingPlaceService.getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(0L);
        Mockito
                .when(bookingPlaceService.getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(99L);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                56L,
                Set.of(12L,99L),
                BookingStatus.PAID
        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Место с идентификатором 99 занято";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceBySessionIdAndIdsAndAvailableFalse(Mockito.anyLong(), Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .create(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet());
    }

    @Test
    void update_ok() {
        Booking updatedEntityBooking = Booking.builder()
                .id(1L)
                .userId("53abe284-8b21-4a44-97a6-2df9f84f6aac")
                .sessionId(6L)
                .bookingStatus(BookingStatus.CREATED)
                .build();
        updatedEntityBooking.setCreatedDate(OffsetDateTime.now());
        updatedEntityBooking.setUpdatedDate(OffsetDateTime.now());
        BookingPlace entityBookingPlace = BookingPlace.builder()
                .id(34L)
                .booking(updatedEntityBooking)
                .placeId(22L)
                .build();
        updatedEntityBooking.setPlaces(List.of(entityBookingPlace));
        SessionResponse sessionResponse = new SessionResponse(
                6L,
                101L,
                "2D",
                2,
                OffsetDateTime.now().plusDays(10),
                true
        );
        PlaceResponse placeResponse = new PlaceResponse(
                22L,
                6L,
                1,
                5,
                "400.00",
                true
        );

        Mockito
                .when(repository.save(Mockito.any(Booking.class)))
                .thenReturn(updatedEntityBooking);
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingOne));
        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(57);
        Mockito
                .when(bookingPlaceService.getIdsForRemove(Mockito.anySet(), Mockito.anySet()))
                .thenReturn(Set.of(1L));
        Mockito
                .when(bookingPlaceService.getIdsForCreate(Mockito.anySet(), Mockito.anySet()))
                .thenReturn(Set.of(6L));
        Mockito
                .doNothing()
                .when(bookingPlaceService)
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());

        Mockito
                .when(bookingPlaceService.getPlaceResponses(Mockito.anySet()))
                .thenReturn(List.of(placeResponse));

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                6L,
                Set.of(22L),
                BookingStatus.CREATED
        );
        BookingResponse response = service.update(1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.id());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", response.userId());
        Assertions.assertEquals(6L, response.session().id());
        Assertions.assertEquals(BookingStatus.CREATED.getValue(), response.status());
        Assertions.assertEquals(1, response.places().size());
        Assertions.assertEquals(22L, response.places().getFirst().id());

        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionClient, Mockito.times(2))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getIdsForRemove(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getIdsForCreate(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceResponses(Mockito.anySet());
    }

    @Test
    void update_badRequestException_statusCancelled() {
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(true);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                8L,
                Set.of(78L),
                BookingStatus.CANCELED
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Невозможно обновить отмененную бронь";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForRemove(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForCreate(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());
    }

    @Test
    void update_entityNotFoundException() {
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                8L,
                Set.of(78L),
                BookingStatus.CREATED
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Бронирование с идентификатором 1 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForRemove(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForCreate(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());
    }

    @Test
    void update_badRequestException_session_notAvailable() {
        SessionResponse sessionResponse = new SessionResponse(
                88L,
                100L,
                "2D",
                4,
                OffsetDateTime.now().plusDays(10),
                false
        );

        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingTwo));
        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                88L,
                Set.of(22L),
                BookingStatus.CREATED
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Сеанс с идентификатором 88 недоступен для бронирования";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForRemove(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForCreate(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());
    }

    @Test
    void update_badRequestException_session_startTime() {
        SessionResponse sessionResponse = new SessionResponse(
                89L,
                100L,
                "2D",
                4,
                OffsetDateTime.now().minusHours(3),
                true
        );

        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingTwo));
        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(107);

        BookingRequest request = new BookingRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                89L,
                Set.of(101L),
                BookingStatus.CREATED
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Бронирование мест невозможно, т.к. сеанс %s в зале %d закончен"
                .formatted(DateUtil.formatDate(sessionResponse.dateTime()), sessionResponse.hall());
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getDurationById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForRemove(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .getIdsForCreate(Mockito.anySet(), Mockito.anySet());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .update(Mockito.anyLong(), Mockito.any(Booking.class), Mockito.anySet(), Mockito.anySet());
    }

    @Test
    void updateStatus_ok() {
        entityBookingOne.setBookingStatus(BookingStatus.CANCELED);

        Mockito
                .when(repository.save(Mockito.any(Booking.class)))
                .thenReturn(entityBookingOne);
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenAnswer(new Answer<Boolean>() {
                    private int count = 0;

                    @Override
                    public Boolean answer(InvocationOnMock invocation) {
                        count = count + 1;
                        return count == 1;
                    }
                });
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingOne));
        Mockito
                .doNothing()
                .when(bookingPlaceService)
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());

        BookingStatusRequest request = new BookingStatusRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                BookingStatus.CANCELED
        );
        BookingResponse response = service.updateStatus(1L, request);
        Assertions.assertEquals(1L, response.id());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", response.userId());
        Assertions.assertEquals(BookingStatus.CANCELED.getValue(), response.status());

        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Booking.class));
        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(3))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void updateStatus_entityNotFoundException() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        BookingStatusRequest request = new BookingStatusRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                BookingStatus.CANCELED
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.updateStatus(5678L, request)
                );
        var expectedMessage = "Бронирование с идентификатором 5678 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void updateStatus_entityNotFoundException_bookingAndUserId() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(false);

        BookingStatusRequest request = new BookingStatusRequest(
                "1241754e-1dcb-4273-bc84-433f084919e0",
                BookingStatus.CANCELED
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.updateStatus(2L, request)
                );
        var expectedMessage = "Бронирование с идентификатором 2 для пользователя 1241754e-1dcb-4273-bc84-433f084919e0 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void updateStatus_badRequestException_statusCancelled() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(true)
                .thenReturn(true);

        BookingStatusRequest request = new BookingStatusRequest(
                "1241754e-1dcb-4273-bc84-433f084919e0",
                BookingStatus.CREATED
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.updateStatus(2L, request)
                );
        var expectedMessage = "Невозможно обновить отмененную бронь";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(2))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void updateStatus_entityAlreadyExists_status() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.exists(ArgumentMatchers.<Specification<Booking>>any()))
                .thenReturn(true)
                .thenReturn(false)
                .thenReturn(true);

        BookingStatusRequest request = new BookingStatusRequest(
                "53abe284-8b21-4a44-97a6-2df9f84f6aac",
                BookingStatus.PAID
        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.updateStatus(1L, request)
                );
        var expectedMessage = "Статус бронирования Paid у брони с идентификатором 1 уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(3))
                .exists(ArgumentMatchers.<Specification<Booking>>any());
        Mockito
                .verify(repository, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(bookingPlaceService, Mockito.times(0))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void deleteById() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityBookingTwo));
        Mockito
                .doNothing()
                .when(repository)
                .delete(Mockito.any(Booking.class));
        Mockito
                .doNothing()
                .when(bookingPlaceService)
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());

        service.deleteById(entityBookingTwo.getId());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .delete(Mockito.any(Booking.class));
        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .updateAvailability(Mockito.anyLong(), Mockito.anySet(), Mockito.anyBoolean());
    }

    @Test
    void sendMessage_createAndUpdate() {
        UserResponse userResponse = new UserResponse(
                "1241754e-1dcb-4273-bc84-433f084919e0",
                "ivan1126",
                "ivan1126@mail.com",
                true,
                "Ivan",
                "Strong",
                "2000-01-01",
                new RoleResponse("445667", "Client"),
                true

        );

        Mockito
                .when(userClient.getById(Mockito.anyString()))
                .thenReturn(userResponse);
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(), Mockito.any(ActionType.class));

        PlaceResponse placeResponseOne = new PlaceResponse(
                1L,
                1L,
                1,
                1,
                "350.00",
                false

        );
        PlaceResponse placeResponseTwo = new PlaceResponse(
                2L,
                15L,
                15,
                3,
                "400.00",
                false
        );
        SessionResponse sessionResponse = new SessionResponse(
                10L,
                93L,
                "2D",
                2,
                OffsetDateTime.now().plusDays(2),
                true
        );
        BookingResponse response = new BookingResponse(
                123L,
                "1241754e-1dcb-4273-bc84-433f084919e0",
                sessionResponse,
                List.of(placeResponseOne, placeResponseTwo),
                "Paid",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        service.sendMessage(response, ActionType.CREATE);
        service.sendMessage(response, ActionType.UPDATE);
        service.sendMessage(response, ActionType.UPDATE_STATUS);

        Mockito
                .verify(userClient, Mockito.times(3))
                .getById(Mockito.anyString());
        Mockito
                .verify(rabbitMQProducer, Mockito.times(3))
                .sendMessage(Mockito.any(ReceiptRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void sendMessage_delete() {
        UserResponse userResponse = new UserResponse(
                "1241754e-1dcb-4273-bc84-433f084919e0",
                "ivan1126",
                "ivan1126@mail.com",
                true,
                "Ivan",
                "Strong",
                "2000-01-01",
                new RoleResponse("445667", "Client"),
                true
        );

        Mockito
                .when(userClient.getById(Mockito.anyString()))
                .thenReturn(userResponse);
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(), Mockito.any(ActionType.class));

        service.sendMessage(123L, "1241754e-1dcb-4273-bc84-433f084919e0");
    }

    @Test
    void buildResponse() {
        PlaceResponse placeResponseOne = new PlaceResponse(
                1L,
                1L,
                1,
                1,
                "350.00",
                false

        );
        PlaceResponse placeResponseTwo = new PlaceResponse(
                2L,
                15L,
                15,
                3,
                "400.00",
                false
        );
        SessionResponse sessionResponse = new SessionResponse(
                10L,
                93L,
                "2D",
                2,
                OffsetDateTime.now().plusDays(2),
                true
        );

        Mockito
                .when(bookingPlaceService.getPlaceResponses(Mockito.anySet()))
                .thenReturn(List.of(placeResponseOne, placeResponseTwo));
        Mockito
                .when(sessionClient.getById(Mockito.anyLong()))
                .thenReturn(sessionResponse);

        BookingResponse response = service.buildResponse(entityBookingOne);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals("53abe284-8b21-4a44-97a6-2df9f84f6aac", response.userId());
        Assertions.assertEquals(sessionResponse, response.session());
        Assertions.assertEquals(List.of(placeResponseOne, placeResponseTwo), response.places());
        Assertions.assertEquals("Paid", response.status());
        Assertions.assertNotNull(response.createdDate());
        Assertions.assertNotNull(response.updatedDate());

        Mockito
                .verify(bookingPlaceService, Mockito.times(1))
                .getPlaceResponses(Mockito.anySet());
        Mockito
                .verify(sessionClient, Mockito.times(1))
                .getById(Mockito.anyLong());
    }
}
