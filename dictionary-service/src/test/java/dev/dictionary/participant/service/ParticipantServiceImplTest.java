package dev.dictionary.participant.service;

import dev.dictionary.participant.entity.Participant;
import dev.dictionary.participant.mapper.ParticipantMapper;
import dev.dictionary.participant.repository.ParticipantRepository;
import dev.dictionary.participant.service.impl.ParticipantServiceImpl;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceImplTest {
    final ParticipantRepository repository = Mockito.mock(ParticipantRepository.class);
    final ParticipantMapper mapper = new ParticipantMapper();
    final ParticipantService service = new ParticipantServiceImpl(repository, mapper);

    Participant entityBen;
    Participant entityOlivia;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorParticipantIdNotFound", "Участник с идентификатором %d не найден");

        entityBen = Participant.builder()
                .id(1L)
                .lastName("Уишоу")
                .firstName("Бен")
                .build();

        entityOlivia = Participant.builder()
                .id(2L)
                .lastName("Коулман")
                .firstName("Оливия")
                .build();
    }

    @Test
    void getAll_ok() {
        List<Participant> participants = List.of(entityBen, entityOlivia);

        Mockito
                .when(repository.findAll())
                .thenReturn(participants);

        List<ParticipantResponse> responses = service.getAll();
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

        List<ParticipantResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getById_ok() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(entityOlivia));

        ParticipantResponse responseCanada = service.getById(2L);
        Assertions.assertNotNull(responseCanada);
        Assertions.assertEquals(2L, responseCanada.id());
        Assertions.assertEquals("Коулман", responseCanada.lastName());
        Assertions.assertEquals("Оливия", responseCanada.firstName());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getById_notFound() {
        Mockito
                .when(repository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getById(1025L)
                );
        String expectedMessage = "Участник с идентификатором 1025 не найден";
        String actualMessage = exception.getApiError().message();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.anyLong());
    }

    @Test
    void getAllByIds_ok() {
        List<Participant> participants = List.of(entityBen, entityOlivia);

        Mockito
                .when(repository.findAllById(Mockito.anyIterable()))
                .thenReturn(participants);

        Set<Long> ids = Set.of(1L, 2L);
        List<ParticipantResponse> participantResponses = service.getAllByIds(ids);
        Assertions.assertEquals(2, participantResponses.size());
        Assertions.assertEquals(1L, participantResponses.get(0).id());
        Assertions.assertEquals(2L, participantResponses.get(1).id());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllById(Mockito.anyIterable());
    }

    @Test
    void getAllByIds_some() {
        List<Participant> participants = List.of(entityBen);
        Mockito
                .when(repository.findAllById(Mockito.anyIterable()))
                .thenReturn(participants);

        Set<Long> ids = Set.of(1L, 1025L);
        List<ParticipantResponse> participantResponses = service.getAllByIds(ids);
        Assertions.assertEquals(1, participantResponses.size());
        Assertions.assertEquals(1L, participantResponses.getFirst().id());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllById(Mockito.anyIterable());
    }

    @Test
    void getAllByIds_empty() {
        Mockito
                .when(repository.findAllById(Mockito.anyIterable()))
                .thenReturn(Collections.emptyList());

        Set<Long> ids = Set.of(1026L, 1025L);
        List<ParticipantResponse> participantResponses = service.getAllByIds(ids);
        Assertions.assertEquals(0, participantResponses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllById(Mockito.anyIterable());
    }

    @Test
    void getNonExistentIds_ok() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(Collections.emptyList());

        Set<Long> ids = Set.of(998L, 1025L);
        List<Long> participantIds = service.getNonExistentIds(ids);
        Assertions.assertEquals(2, participantIds.size());
        Assertions.assertTrue(participantIds.contains(998L));
        Assertions.assertTrue(participantIds.contains(1025L));

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
    }

    @Test
    void getNonExistentIds_some() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(1L));

        Set<Long> ids = Set.of(1L, 1025L);
        List<Long> participantIds = service.getNonExistentIds(ids);
        Assertions.assertEquals(1, participantIds.size());
        Assertions.assertEquals(1025L, participantIds.getFirst());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
    }

    @Test
    void getNonExistentIds_empty() {
        Mockito
                .when(repository.findExistentIds(Mockito.anyIterable()))
                .thenReturn(List.of(1L, 2L));

        Set<Long> ids = Set.of(1L, 2L);
        List<Long> participantIds = service.getNonExistentIds(ids);
        Assertions.assertEquals(0, participantIds.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findExistentIds(Mockito.anyIterable());
    }
}
