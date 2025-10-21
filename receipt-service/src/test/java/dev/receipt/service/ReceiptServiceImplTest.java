package dev.receipt.service;

import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.booking.client.BookingClient;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.notification.dto.NotificationDeleteRequest;
import dev.library.domain.notification.dto.NotificationRequest;
import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.receipt.dto.ReceiptRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.user.client.UserClient;
import dev.library.domain.user.dto.RoleResponse;
import dev.library.domain.user.dto.UserResponse;
import dev.receipt.entity.Receipt;
import dev.receipt.repository.ReceiptRepository;
import dev.receipt.service.impl.ReceiptServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ReceiptServiceImplTest {
    final ReceiptRepository repository = Mockito.mock(ReceiptRepository.class);
    final TemplateService templateService = Mockito.mock(TemplateService.class);
    final GenerateDocumentService documentService = Mockito.mock(GenerateDocumentService.class);
    final MovieClient movieClient = Mockito.mock(MovieClient.class);
    final UserClient userClient = Mockito.mock(UserClient.class);
    final BookingClient bookingClient = Mockito.mock(BookingClient.class);
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final ReceiptService service = new ReceiptServiceImpl(repository, templateService, documentService, movieClient,
            userClient, bookingClient, rabbitMQProducer);

    UUID idReceipt;

    Receipt entityReceipt;

    BookingResponse bookingResponse;
    SessionResponse sessionResponse;
    PlaceResponse placeResponse;
    UserResponse userResponse;
    GenreResponse genreResponse;
    CountryResponse countryResponse;
    ParticipantResponse participantResponseDirector;
    ParticipantResponse participantResponseActor;
    MovieResponse movieResponse;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorReceiptBookingIdNotFound", "Запись о квитанции с идентификатором бронирования %d не найдена");
        ReflectionTestUtils.setField(service, "errorReceiptBookingIdAlreadyExists", "Запись о квитанции с идентификатором %d уже существует");

        idReceipt = UUID.randomUUID();

        entityReceipt = Receipt.builder()
                .id(idReceipt)
                .bookingId(112L)
                .data(new byte[]{1,2,3})
                .build();

        sessionResponse = new SessionResponse(
                2L,
                125L,
                "3D",
                4,
                OffsetDateTime.now().plusDays(2),
                true
        );
        placeResponse = new PlaceResponse(
                1L,
                2L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);
        userResponse = new UserResponse(
                UUID.randomUUID().toString(),
                "max1234",
                "max1234@mail.com",
                true,
                "Макс",
                "Булочкин",
                "1999-01-01",
                new RoleResponse(UUID.randomUUID().toString(), "client"),
                true
        );
        genreResponse = new GenreResponse(
                1L,
                "Боевик"
        );
        countryResponse = new CountryResponse(
                1L,
                "999",
                "Тест"
        );
        participantResponseDirector = new ParticipantResponse(
                1L,
                "Петров",
                "Андрей",
                "Иванович"
        );
        participantResponseDirector = new ParticipantResponse(
                2L,
                "Крутой",
                "Майкл"
        );
        movieResponse = new MovieResponse(
                56L,
                "Тест",
                "Тест",
                111,
                2022,
                "18+",
                false,
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );
        movieResponse.genres().add(genreResponse);
        movieResponse.countries().add(countryResponse);
        movieResponse.directors().add(participantResponseDirector);
        movieResponse.actors().add(participantResponseActor);

        Mockito
                .when(movieClient.getById(Mockito.anyLong()))
                .thenReturn(movieResponse);
        Mockito
                .when(templateService.createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class)))
                .thenReturn("<html><p>Content</p></html>");
        Mockito
                .when(documentService.generateReceipt(Mockito.anyString()))
                .thenReturn("<html><p>Content</p></html>".getBytes());
        Mockito
                .when(repository.save(Mockito.any(Receipt.class)))
                .thenReturn(entityReceipt);
    }

    @Test
    void getByBookingId_ok() {
        Mockito
                .when(repository.findDataByBookingId(Mockito.anyLong()))
                .thenReturn(Optional.of(new byte[]{1,2,3}));

        Resource data = service.getByBookingId(1L);
        Assertions.assertNotNull(data);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDataByBookingId(Mockito.anyLong());
    }

    @Test
    void getByBookingId_create() {
        Mockito
                .when(repository.findDataByBookingId(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingClient.getById(Mockito.anyLong()))
                .thenReturn(bookingResponse);
        Mockito
                .when(userClient.getById(Mockito.anyString()))
                .thenReturn(userResponse);

        Resource data = service.getByBookingId(112L);
        Assertions.assertNotNull(data);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDataByBookingId(Mockito.anyLong());
        Mockito
                .verify(bookingClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(userClient, Mockito.times(1))
                .getById(Mockito.anyString());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(1))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(1))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Receipt.class));
    }

    @Test
    void getByBookingId_badRequestException_session_greaterThanTwo() {
        PlaceResponse placeResponseTwo = new PlaceResponse(
                1L,
                3L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse.places().add(placeResponseTwo);

        Mockito
                .when(repository.findDataByBookingId(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingClient.getById(Mockito.anyLong()))
                .thenReturn(bookingResponse);

        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.getByBookingId(112L)
                );
        var expectedMessage = "Бронирование не может иметь места с разными сеансами";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDataByBookingId(Mockito.anyLong());
        Mockito
                .verify(bookingClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(userClient, Mockito.times(1))
                .getById(Mockito.anyString());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Receipt.class));
    }

    @Test
    void getByBookingId_badRequestException_differentSessions() {
        placeResponse = new PlaceResponse(
                1L,
                25L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.findDataByBookingId(Mockito.anyLong()))
                .thenReturn(Optional.empty());
        Mockito
                .when(bookingClient.getById(Mockito.anyLong()))
                .thenReturn(bookingResponse);

        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.getByBookingId(112L)
                );
        var expectedMessage = "Сеанс в бронировании и у мест не может быть разным";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findDataByBookingId(Mockito.anyLong());
        Mockito
                .verify(bookingClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(userClient, Mockito.times(1))
                .getById(Mockito.anyString());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Receipt.class));
    }

    @Test
    void create_ok() {
        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(false);
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        service.create(request);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(1))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(1))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Receipt.class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void create_entityAlreadyExistsException() {
        bookingResponse = new BookingResponse(
                1L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(true);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Запись о квитанции с идентификатором 1 уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Receipt.class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void create_badRequestException_session_greaterThanTwo() {
        PlaceResponse placeResponseTwo = new PlaceResponse(
                1L,
                3L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse.places().add(placeResponseTwo);

        Mockito
                .when(repository.existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(false);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Бронирование не может иметь места с разными сеансами";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Receipt.class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void create_badRequestException_differentSessions() {
        placeResponse = new PlaceResponse(
                1L,
                25L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(false);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Сеанс в бронировании и у мест не может быть разным";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);


        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingIdAndUserId(Mockito.anyLong(), Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Receipt.class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void update_ok() {
        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(repository)
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        service.update(request);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(1))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(1))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(1))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void update_entityNotFoundException() {
        bookingResponse = new BookingResponse(
                9999L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(false);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(request)
                );
        var expectedMessage = "Запись о квитанции с идентификатором бронирования 9999 не найдена";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void update_badRequestException_session_greaterThanTwo() {
        PlaceResponse placeResponseTwo = new PlaceResponse(
                1L,
                3L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse.places().add(placeResponseTwo);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(request)
                );
        var expectedMessage = "Бронирование не может иметь места с разными сеансами";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void update_badRequestException_differentSessions() {
        placeResponse = new PlaceResponse(
                1L,
                25L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(request)
                );
        var expectedMessage = "Сеанс в бронировании и у мест не может быть разным";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void updateStatus_ok() {
        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(repository)
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        service.updateStatus(request);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(1))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(1))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(1))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void updateStatus_entityNotFoundException() {
        bookingResponse = new BookingResponse(
                9999L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Created",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(false);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.updateStatus(request)
                );
        var expectedMessage = "Запись о квитанции с идентификатором бронирования 9999 не найдена";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void updateStatus_badRequestException_session_greaterThanTwo() {
        PlaceResponse placeResponseTwo = new PlaceResponse(
                1L,
                3L,
                1,
                1,
                "200.00",
                true
        );
        bookingResponse.places().add(placeResponseTwo);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.updateStatus(request)
                );
        var expectedMessage = "Бронирование не может иметь места с разными сеансами";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void updateStatus_badRequestException_differentSessions() {
        placeResponse = new PlaceResponse(
                1L,
                25L,
                1,
                1,
                "300.00",
                true
        );
        bookingResponse = new BookingResponse(
                112L,
                UUID.randomUUID().toString(),
                sessionResponse,
                new ArrayList<>(),
                "Paid",
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
        bookingResponse.places().add(placeResponse);

        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);

        ReceiptRequest request = new ReceiptRequest(
                bookingResponse,
                userResponse
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.updateStatus(request)
                );
        var expectedMessage = "Сеанс в бронировании и у мест не может быть разным";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(0))
                .getById(Mockito.anyLong());
        Mockito
                .verify(templateService, Mockito.times(0))
                .createContent(Mockito.any(BookingResponse.class),
                        Mockito.any(MovieResponse.class), Mockito.any(UserResponse.class));
        Mockito
                .verify(documentService, Mockito.times(0))
                .generateReceipt(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(0))
                .updateDataByBookingId(Mockito.anyLong(), Mockito.any(byte[].class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void deleteByBookingId_ok() {
        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(repository)
                .deleteByBookingId(Mockito.anyLong());
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.any(NotificationDeleteRequest.class), Mockito.any(ActionType.class));

        NotificationDeleteRequest request = new NotificationDeleteRequest(
                112L,
                userResponse
        );
        service.deleteByBookingId(request);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .deleteByBookingId(Mockito.anyLong());
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.any(NotificationDeleteRequest.class), Mockito.any(ActionType.class));
    }

    @Test
    void deleteByBookingId_entityNotFoundException() {
        Mockito
                .when(repository.existsByBookingId(Mockito.anyLong()))
                .thenReturn(false);

        NotificationDeleteRequest request = new NotificationDeleteRequest(
                9999L,
                userResponse
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.deleteByBookingId(request)
                );
        var expectedMessage = "Запись о квитанции с идентификатором бронирования 9999 не найдена";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByBookingId(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .deleteByBookingId(Mockito.anyLong());
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.any(NotificationDeleteRequest.class), Mockito.any(ActionType.class));
    }
}
