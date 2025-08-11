package dev.session.service;

import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.session.dto.*;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.session.entity.Place;
import dev.session.entity.Session;
import dev.session.mapper.PlaceMapper;
import dev.session.repository.PlaceRepository;
import dev.session.service.impl.PlaceServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceImplTest {
    final PlaceRepository repository = Mockito.mock(PlaceRepository.class);
    final PlaceMapper mapper = new PlaceMapper();
    final SessionService sessionService = Mockito.mock(SessionService.class);
    final PlaceService service = new PlaceServiceImpl(repository, mapper, sessionService);

    Session entitySessionOne;
    Session entitySessionTwo;

    Place entityPlaceOne;
    Place entityPlaceTwo;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorPlaceIdNotFound", "Место с идентификатором %s не найдено");
        ReflectionTestUtils.setField(service, "errorPlaceSessionIdAndRowAndNumberAlreadyExists", "Место в ряду %d с номером %d уже существует");

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

        entityPlaceOne = Place.builder()
                .id(1L)
                .session(entitySessionOne)
                .row(1)
                .number(1)
                .price(BigDecimal.valueOf(123))
                .available(true)
                .build();
        entityPlaceTwo = Place.builder()
                .id(26L)
                .session(entitySessionOne)
                .row(1)
                .number(1)
                .price(BigDecimal.valueOf(129))
                .available(false)
                .build();
    }

    @Test
    void getAll_ok() {
        List<Place> places = List.of(entityPlaceOne, entityPlaceTwo);

        Mockito
                .when(repository.findAll())
                .thenReturn(places);

        List<PlaceResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll())
                .thenReturn(Collections.emptyList());

        List<PlaceResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAllBySession_Id_ok() {
        Mockito
                .when(repository.findAllBySession_Id(Mockito.anyLong()))
                .thenReturn(List.of(entityPlaceOne));

        List<PlaceResponse> responses = service.getAllBySession_Id(1L);
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(1, responses.getFirst().id());
        Assertions.assertEquals(1, responses.getFirst().sessionId());
        Assertions.assertEquals(1, responses.getFirst().row());
        Assertions.assertEquals(1, responses.getFirst().number());
        Assertions.assertEquals("123.00", responses.getFirst().price());
        Assertions.assertEquals(true, responses.getFirst().available());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllBySession_Id(Mockito.anyLong());
    }

    @Test
    void getAllBySession_Id_empty() {
        Mockito
                .when(repository.findAllBySession_Id(Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        List<PlaceResponse> responses = service.getAllBySession_Id(1199L);
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllBySession_Id(Mockito.anyLong());
    }

    @Test
    void getAllByIds_ok() {
        List<Place> places = List.of(entityPlaceOne, entityPlaceTwo);

        Mockito
                .when(repository.findAllByIds(Mockito.anyIterable()))
                .thenReturn(places);

        List<PlaceResponse> responses = service.getAllByIds(Set.of(1L, 26L));
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByIds(Mockito.anyIterable());
    }

    @Test
    void getAllByIds_some() {
        List<Place> places = List.of(entityPlaceTwo);

        Mockito
                .when(repository.findAllByIds(Mockito.anyIterable()))
                .thenReturn(places);

        List<PlaceResponse> responses = service.getAllByIds(Set.of(26L, 1199L));
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(1, responses.size());
        Assertions.assertEquals(26, responses.getFirst().id());
        Assertions.assertEquals(1, responses.getFirst().sessionId());
        Assertions.assertEquals(1, responses.getFirst().row());
        Assertions.assertEquals(1, responses.getFirst().number());
        Assertions.assertEquals("129.00", responses.getFirst().price());
        Assertions.assertEquals(false, responses.getFirst().available());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByIds(Mockito.anyIterable());
    }

    @Test
    void getAllByIds_empty() {
        Mockito
                .when(repository.findAllByIds(Mockito.anyIterable()))
                .thenReturn(Collections.emptyList());

        List<PlaceResponse> responses = service.getAllByIds(Set.of(11345L, 1199L));
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByIds(Mockito.anyIterable());
    }

    @Test
    void getById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityPlaceTwo));

        PlaceResponse response = service.getById(26L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(26, response.id());
        Assertions.assertEquals(1, response.sessionId());
        Assertions.assertEquals(1, response.row());
        Assertions.assertEquals(1, response.number());
        Assertions.assertEquals("129.00", response.price());
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
        var expectedMessage = "Место с идентификатором 123456 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void create_ok() {
        Place entity = Place.builder()
                .id(45L)
                .session(entitySessionOne)
                .row(3)
                .number(15)
                .price(BigDecimal.valueOf(445.78))
                .available(true)
                .build();

        Mockito
                .when(repository.save(Mockito.any(Place.class)))
                .thenReturn(entity);
        Mockito
                .when(repository.existsBySession_IdAndRowAndNumber(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(false);
        Mockito
                .when(sessionService.findById(Mockito.anyLong()))
                .thenReturn(entitySessionOne);

        PlaceRequest request = new PlaceRequest(
                1L, 3, 15, BigDecimal.valueOf(445.78), true
        );
        PlaceResponse response = service.create(request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(45L, response.id());
        Assertions.assertEquals(1, response.sessionId());
        Assertions.assertEquals(3, response.row());
        Assertions.assertEquals(15, response.number());
        Assertions.assertEquals("445.78", response.price());
        Assertions.assertEquals(true, response.available());

        Mockito
                .verify(repository, Mockito.times(1))
                .existsBySession_IdAndRowAndNumber(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito
                .verify(sessionService, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Place.class));
    }

    @Test
    void create_entityAlreadyExistsException() {
        Mockito
                .when(repository.existsBySession_IdAndRowAndNumber(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(true);

        PlaceRequest request = new PlaceRequest(
                1L, 4, 17, BigDecimal.valueOf(445.78), false
        );
        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(request)
                );
        var expectedMessage = "Место в ряду 4 с номером 17 уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsBySession_IdAndRowAndNumber(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
        Mockito
                .verify(sessionService, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Place.class));
    }

    @Test
    void update_ok() {
        Place entity = Place.builder()
                .id(1L)
                .session(entitySessionTwo)
                .row(4)
                .number(21)
                .price(BigDecimal.valueOf(500))
                .available(false)
                .build();

        Mockito
                .when(repository.save(Mockito.any(Place.class)))
                .thenReturn(entity);
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityPlaceOne));
        Mockito
                .when(sessionService.findById(Mockito.anyLong()))
                .thenReturn(entitySessionTwo);

        PlaceRequest request = new PlaceRequest(
                2L, 4, 21, BigDecimal.valueOf(500), false
        );
        PlaceResponse response = service.update(1L, request);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.id());
        Assertions.assertEquals(2, response.sessionId());
        Assertions.assertEquals(4, response.row());
        Assertions.assertEquals(21, response.number());
        Assertions.assertEquals("500.00", response.price());
        Assertions.assertEquals(false, response.available());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionService, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Place.class));
    }

    @Test
    void update_entityNotFoundException_place() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        PlaceRequest request = new PlaceRequest(
                2L, 5, 26, BigDecimal.valueOf(500), true
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(1199L, request)
                );
        var expectedMessage = "Место с идентификатором 1199 не найдено";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionService, Mockito.times(0))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Place.class));
    }

    @Test
    void update_entityNotFoundException_session() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityPlaceOne));
        Mockito
                .when(sessionService.findById(Mockito.anyLong()))
                .thenThrow(new EntityNotFoundException("Сеанс с идентификатором 34567 не найден"));

        PlaceRequest request = new PlaceRequest(
                34567L, 5, 26, BigDecimal.valueOf(500), true
        );
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.update(1L, request)
                );
        var expectedMessage = "Сеанс с идентификатором 34567 не найден";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(sessionService, Mockito.times(1))
                .findById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(0))
                .save(Mockito.any(Place.class));
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

        service.deleteById(1L);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsById(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
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
        var expectedMessage = "Место с идентификатором 123456 не найдено";
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
    void updateAvailable() {
        Mockito
                .doNothing()
                .when(repository)
                .updateAvailable(Mockito.anyLong(), Mockito.anyIterable(), Mockito.anyBoolean());

        service.updateAvailable(2L, Set.of(26L), false);

        Mockito
                .verify(repository, Mockito.times(1))
                .updateAvailable(Mockito.anyLong(), Mockito.anyIterable(), Mockito.anyBoolean());
    }

    @Test
    void getPlaceNotEqualsSessionBySessionIdAndIds_ok() {
        Mockito
                .when(repository.findPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(Optional.of(99L));

        Long response = service.getPlaceNotEqualsSessionBySessionIdAndIds(2L, Set.of(26L, 99L));
        Assertions.assertEquals(99, response);

        Mockito
                .verify(repository, Mockito.times(1))
                .findPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
    }

    @Test
    void getPlaceNotEqualsSessionBySessionIdAndIds_zero() {
        Mockito
                .when(repository.findPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet()))
                .thenReturn(Optional.empty());

        Long response = service.getPlaceNotEqualsSessionBySessionIdAndIds(2L, Set.of(26L));
        Assertions.assertEquals(0, response);

        Mockito
                .verify(repository, Mockito.times(1))
                .findPlaceNotEqualsSessionBySessionIdAndIds(Mockito.anyLong(), Mockito.anySet());
    }

    @Test
    void getPlaceBySessionIdAndIdsAndAvailable_ok() {
        Mockito
                .when(repository.findPlaceBySessionIdAndAvailableAndIds(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(Optional.of(1L));

        Long response = service.getPlaceBySessionIdAndIdsAndAvailable(1L, Set.of(1L, 2L), false);
        Assertions.assertEquals(1, response);

        Mockito
                .verify(repository, Mockito.times(1))
                .findPlaceBySessionIdAndAvailableAndIds(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anySet());
    }

    @Test
    void getPlaceBySessionIdAndIdsAndAvailable_zero() {
        Mockito
                .when(repository.findPlaceBySessionIdAndAvailableAndIds(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anySet()))
                .thenReturn(Optional.empty());

        Long response = service.getPlaceBySessionIdAndIdsAndAvailable(2L, Set.of(99L, 100L), true);
        Assertions.assertEquals(0, response);

        Mockito
                .verify(repository, Mockito.times(1))
                .findPlaceBySessionIdAndAvailableAndIds(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anySet());
    }
}
