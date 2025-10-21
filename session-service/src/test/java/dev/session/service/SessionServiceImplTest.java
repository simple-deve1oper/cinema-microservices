package dev.session.service;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.core.specification.SpecificationBuilder;
import dev.library.core.util.DateUtil;
import dev.library.domain.movie.client.MovieClient;
import dev.library.domain.rabbitmq.constant.ActionType;
import dev.library.domain.rabbitmq.constant.ScheduleType;
import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.session.dto.SessionSearchRequest;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.session.entity.Session;
import dev.session.mapper.SessionMapper;
import dev.session.repository.SessionRepository;
import dev.session.service.impl.SessionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class SessionServiceImplTest {
    final SessionRepository repository = Mockito.mock(SessionRepository.class);
    final SessionMapper mapper = new SessionMapper();
    final SpecificationBuilder<Session> specificationBuilder = new SpecificationBuilder<>();
    final MovieClient movieClient = Mockito.mock(MovieClient.class);
    final RabbitMQProducer rabbitMQProducer = Mockito.mock(RabbitMQProducer.class);
    final SessionService service = new SessionServiceImpl(repository, mapper, specificationBuilder, movieClient, rabbitMQProducer);

    Session entitySessionOne;
    Session entitySessionTwo;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorSessionIdNotFound", "Сеанс с идентификатором %d не найден");
        ReflectionTestUtils.setField(service, "errorSessionHallAndDateTimeAlreadyExists", "Сеанс в зале %d на время %s уже существует");
        ReflectionTestUtils.setField(service, "errorSessionMovieIdNotFound", "Фильм с идентификатором %d не найден");

        entitySessionOne = Session.builder()
                .id(1L)
                .movieId(1L)
                .movieFormat(MovieFormat.TWO_D)
                .hall(2)
                .dateTime(OffsetDateTime.now().plusDays(1))
                .available(false)
                .build();
        entitySessionTwo = Session.builder()
                .id(2L)
                .movieId(1L)
                .movieFormat(MovieFormat.THREE_D)
                .hall(1)
                .dateTime(OffsetDateTime.now())
                .available(true)
                .build();
    }

    @Test
    void getAll_ok() {
        List<Session> sessions = List.of(entitySessionOne, entitySessionTwo);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Session>>any()))
                .thenReturn(sessions);

        List<SessionResponse> responses = service.getAll(new SessionSearchRequest());
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Session>>any());
    }

    @Test
    void getAll_some() {
        List<Session> sessions = List.of(entitySessionTwo);

        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Session>>any()))
                .thenReturn(sessions);

        List<SessionResponse> responses = service.getAll(
                new SessionSearchRequest(1L, LocalDate.now())
        );
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(2L, responses.getFirst().id());
        Assertions.assertEquals(1L, responses.getFirst().movieId());
        Assertions.assertEquals(LocalDate.now(), responses.getFirst().dateTime().toLocalDate());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Session>>any());
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll(ArgumentMatchers.<Specification<Session>>any()))
                .thenReturn(Collections.emptyList());

        List<SessionResponse> responses = service.getAll(
                new SessionSearchRequest(1L, LocalDate.now())
        );
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll(ArgumentMatchers.<Specification<Session>>any());
    }

    @Test
    void getById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entitySessionOne));

        SessionResponse response = service.getById(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals(1L, response.movieId());
        Assertions.assertEquals("2D", response.movieFormat());
        Assertions.assertEquals(2, response.hall());
        Assertions.assertNotNull(response.dateTime());
        Assertions.assertEquals(false, response.available());

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
        var expectedMessage = "Сеанс с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void create_ok() {
        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(7);
        Session entity = Session.builder()
                .id(3L)
                .movieId(2L)
                .movieFormat(MovieFormat.THREE_D)
                .hall(3)
                .dateTime(dateTime)
                .available(false)
                .build();

        Mockito
                .when(repository.save(Mockito.any(Session.class)))
                .thenReturn(entity);
        Mockito
                .when(movieClient.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class)))
                .thenReturn(false);

        SessionRequest request = new SessionRequest(
                2L,
                MovieFormat.THREE_D,
                3,
                dateTime,
                false
        );
        SessionResponse response = service.create(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(3, response.id());
        Assertions.assertEquals(2L, response.movieId());
        Assertions.assertEquals(MovieFormat.THREE_D.getValue(), response.movieFormat());
        Assertions.assertNotNull(response.dateTime());
        Assertions.assertEquals(false, response.available());

        Mockito
                .verify(movieClient, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class));
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Session.class));
        Mockito
                .verify(rabbitMQProducer, Mockito.times(0))
                .sendMessage(Mockito.anyString(), Mockito.any(OffsetDateTime.class), Mockito.any(ActionType.class),
                        Mockito.any(ScheduleType.class));
    }

    @Test
    void create_entityNotFoundException_movie() {
        Mockito
                .when(movieClient.existsById(Mockito.anyLong()))
                .thenReturn(false);

        SessionRequest request = new SessionRequest(
                1199L,
                MovieFormat.TWO_D,
                4, OffsetDateTime.now(),
                true
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Фильм с идентификатором 1199 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(movieClient, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class));
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Session.class));
    }

    @Test
    void create_entityAlreadyExistsException_hallAndDateTime() {
        Mockito
                .when(movieClient.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class)))
                .thenReturn(true);

        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(2);
        SessionRequest request = new SessionRequest(
                2L, MovieFormat.TWO_D, 2, dateTime, true);
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Сеанс в зале %d на время %s уже существует".formatted(2, DateUtil.formatDate(dateTime));
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(movieClient, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class));
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Session.class));
    }

    @Test
    void update_ok() {
        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(10);
        SessionRequest request = new SessionRequest(1L, MovieFormat.TWO_D, 1, dateTime, true);
        Session entity = Session.builder()
                .id(1L)
                .movieId(1L)
                .movieFormat(MovieFormat.TWO_D)
                .hall(1)
                .dateTime(dateTime)
                .available(true)
                .build();

        Mockito
                .when(repository.save(Mockito.any(Session.class)))
                .thenReturn(entity);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entitySessionOne));
        Mockito
                .when(movieClient.getDurationById(Mockito.anyLong()))
                .thenReturn(1);
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.anyString(), Mockito.any(OffsetDateTime.class), Mockito.any(ActionType.class),
                        Mockito.any(ScheduleType.class));

        SessionResponse response = service.update(1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals(1L, response.movieId());
        Assertions.assertEquals(MovieFormat.TWO_D.getValue(), response.movieFormat());
        Assertions.assertEquals(dateTime, response.dateTime());
        Assertions.assertEquals(true, response.available());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Session.class));
        Mockito
                .verify(movieClient, Mockito.times(1))
                        .getDurationById(Mockito.anyLong());
        Mockito
                .verify(rabbitMQProducer, Mockito.times(2))
                .sendMessage(Mockito.anyString(), Mockito.any(OffsetDateTime.class), Mockito.any(ActionType.class),
                        Mockito.any(ScheduleType.class));
    }

    @Test
    void update_entityNotFoundException() {
        SessionRequest request = new SessionRequest(6L, MovieFormat.TWO_D, 3, OffsetDateTime.now(), false);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(1199L, request)
                );
        var expectedMessage = "Сеанс с идентификатором 1199 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Session.class));
    }

    @Test
    void update_entityNotFoundException_movie() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entitySessionTwo));
        Mockito
                .when(movieClient.existsById(Mockito.anyLong()))
                .thenReturn(false);

        SessionRequest request = new SessionRequest(
                1199L,
                MovieFormat.TWO_D,
                4, OffsetDateTime.now(),
                true
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(2L, request)
                );
        var expectedMessage = "Фильм с идентификатором 1199 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class));
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Session.class));
    }

    @Test
    void update_entityAlreadyExistsException_hallAndDateTime() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entitySessionTwo));
        Mockito
                .when(movieClient.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .when(repository.existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class)))
                .thenReturn(true);

        OffsetDateTime dateTime = OffsetDateTime.now().plusDays(2);
        SessionRequest request = new SessionRequest(
                2L, MovieFormat.TWO_D, 2, dateTime, true);
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Сеанс в зале %d на время %s уже существует".formatted(2, DateUtil.formatDate(dateTime));
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(movieClient, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .existsByHallAndDateTime(Mockito.anyInt(), Mockito.any(OffsetDateTime.class));
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Session.class));
    }

    @Test
    void deleteById_ok() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(true);
        Mockito
                .doNothing()
                .when(repository)
                .deleteById(Mockito.anyLong());
        Mockito
                .doNothing()
                .when(rabbitMQProducer)
                .sendMessage(Mockito.anyString());

        service.deleteById(1L);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
        Mockito
                .verify(rabbitMQProducer, Mockito.times(1))
                .sendMessage(Mockito.anyString());
    }

    @Test
    void deleteById_entityNotFoundException() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.deleteById(123456L)
                );
        var expectedMessage = "Сеанс с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .deleteById(Mockito.anyLong());
    }

    @Test
    void findById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entitySessionOne));

        Session entity = service.findById(1L);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(1, entity.getId());
        Assertions.assertEquals(1L, entity.getMovieId());
        Assertions.assertEquals(MovieFormat.TWO_D, entity.getMovieFormat());
        Assertions.assertEquals(2, entity.getHall());
        Assertions.assertNotNull(entity.getDateTime());
        Assertions.assertEquals(false, entity.getAvailable());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void findById_entityNotFoundException() {
        Mockito
                .when(repository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.findById(123456L)
                );
        var expectedMessage = "Сеанс с идентификатором 123456 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }
}
