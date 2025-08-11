package dev.movie.service;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.dictionary.participant.client.ParticipantClient;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieParticipant;
import dev.movie.mapper.MovieParticipantMapper;
import dev.movie.repository.MovieParticipantRepository;
import dev.movie.service.impl.MovieParticipantServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

@ExtendWith(MockitoExtension.class)
public class MovieParticipantServiceImplTest {
    final MovieParticipantRepository repository = Mockito.mock(MovieParticipantRepository.class);
    final MovieParticipantMapper mapper = new MovieParticipantMapper();
    final ParticipantClient participantClient = Mockito.mock(ParticipantClient.class);
    final MovieParticipantService service = new MovieParticipantServiceImpl(repository, mapper, participantClient);

    MovieParticipant entityDirector;
    MovieParticipant entityActor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "participantIdsNotFound", "Переданы несуществующие идентификаторы участников: %s");

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();

        entityDirector = MovieParticipant.builder()
                .id(13L)
                .movie(movie)
                .participantId(12L)
                .position(Position.DIRECTOR)
                .build();

        entityActor = MovieParticipant.builder()
                .id(14L)
                .movie(movie)
                .participantId(13L)
                .position(Position.ACTOR)
                .build();
    }

    @Test
    void create_ok() {
        Mockito
                .when(participantClient.getNonExistentIds(Mockito.anySet()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(repository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(entityDirector, entityActor));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Map<Position, Set<Long>> mapParticipants = Map.of(
                Position.DIRECTOR, Set.of(12L),
                Position.ACTOR, Set.of(13L)
        );
        List<MovieParticipant> movieParticipants = service.create(movie, mapParticipants);
        Assertions.assertNotNull(movieParticipants);
        Assertions.assertFalse(movieParticipants.isEmpty());
        Assertions.assertEquals(2, movieParticipants.size());
        Assertions.assertEquals(13L, movieParticipants.getFirst().getId());
        Assertions.assertEquals(12, movieParticipants.getFirst().getParticipantId());
        Assertions.assertEquals(Position.DIRECTOR, movieParticipants.getFirst().getPosition());
        Assertions.assertEquals(14L, movieParticipants.get(1).getId());
        Assertions.assertEquals(13, movieParticipants.get(1).getParticipantId());
        Assertions.assertEquals(Position.ACTOR, movieParticipants.get(1).getPosition());

        Mockito
                .verify(participantClient, Mockito.times(1))
                .getNonExistentIds(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }

    @Test
    void create_badRequestException_checkNonExistentIds() {
        Mockito
                .when(participantClient.getNonExistentIds(Mockito.anySet()))
                .thenReturn(List.of(1011L, 1001L));

        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        Map<Position, Set<Long>> mapParticipants = Map.of(
                Position.DIRECTOR, Set.of(1011L),
                Position.ACTOR, Set.of(1001L)
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.create(movie, mapParticipants)
                );
        var expectedMessage = "Переданы несуществующие идентификаторы участников: [1011, 1001]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(participantClient, Mockito.times(1))
                .getNonExistentIds(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .saveAll(Mockito.anyList());
    }

    @Test
    void update_ok() {
        Mockito
                .when(participantClient.getNonExistentIds(Mockito.anySet()))
                .thenReturn(Collections.emptyList());
        Mockito
                .when(repository.saveAll(Mockito.anyList()))
                .thenReturn(List.of(entityActor));

        List<MovieParticipant> oldMovieParticipants = new ArrayList<>();
        oldMovieParticipants.add(
                MovieParticipant.builder()
                        .id(999L)
                        .participantId(999L)
                        .position(Position.DIRECTOR)
                        .build()
        );
        oldMovieParticipants.add(
                MovieParticipant.builder()
                        .id(997L)
                        .participantId(997L)
                        .position(Position.ACTOR)
                        .build()
        );
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .participants(oldMovieParticipants)
                .build();
        for (MovieParticipant oldMovieParticipant : oldMovieParticipants) {
            oldMovieParticipant.setMovie(movie);
        }
        Map<Position, Set<Long>> mapParticipants = Map.of(
                Position.DIRECTOR, Set.of(999L),
                Position.ACTOR, Set.of(13L)
        );
        service.update(movie, mapParticipants);
        Assertions.assertEquals(2, movie.getParticipants().size());
        Assertions.assertEquals(999, movie.getParticipants().getFirst().getParticipantId());
        Assertions.assertEquals(Position.DIRECTOR, movie.getParticipants().getFirst().getPosition());
        Assertions.assertEquals(999, movie.getParticipants().getFirst().getId());
        Assertions.assertEquals(13, movie.getParticipants().get(1).getParticipantId());
        Assertions.assertEquals(Position.ACTOR, movie.getParticipants().get(1).getPosition());
        Assertions.assertEquals(14, movie.getParticipants().get(1).getId());

        Mockito
                .verify(participantClient, Mockito.times(1))
                .getNonExistentIds(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(1))
                .saveAll(Mockito.anyList());
    }

    @Test
    void update_badRequestException_checkNonExistentIds() {
        Mockito
                .when(participantClient.getNonExistentIds(Mockito.anySet()))
                .thenReturn(List.of(1001L));

        List<MovieParticipant> oldMovieParticipants = new ArrayList<>();
        oldMovieParticipants.add(
                MovieParticipant.builder()
                        .id(999L)
                        .participantId(999L)
                        .position(Position.DIRECTOR)
                        .build()
        );
        oldMovieParticipants.add(
                MovieParticipant.builder()
                        .id(997L)
                        .participantId(997L)
                        .position(Position.ACTOR)
                        .build()
        );
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .participants(oldMovieParticipants)
                .build();
        for (MovieParticipant oldMovieParticipant : oldMovieParticipants) {
            oldMovieParticipant.setMovie(movie);
        }
        Map<Position, Set<Long>> mapParticipants = Map.of(
                Position.DIRECTOR, Set.of(1001L),
                Position.ACTOR, Set.of(13L)
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.update(movie, mapParticipants)
                );
        var expectedMessage = "Переданы несуществующие идентификаторы участников: [1001]";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(participantClient, Mockito.times(1))
                .getNonExistentIds(Mockito.anySet());
        Mockito
                .verify(repository, Mockito.times(0))
                .saveAll(Mockito.anyList());
    }

    @Test
    void getParticipantResponseByMovieIdAndPosition() {
        List<ParticipantResponse> responses = List.of(
                new ParticipantResponse(12L, "Иванов", "Иван", "Иванович"),
                new ParticipantResponse(13L, "Воробей", "Джек")
        );

        Mockito
                .when(participantClient.getAllByIds(Mockito.anySet()))
                .thenReturn(responses);

        List<ParticipantResponse> participantResponses = service.getParticipantResponseByMovieIdAndPosition(List.of(entityDirector, entityActor));
        Assertions.assertNotNull(participantResponses);
        Assertions.assertFalse(participantResponses.isEmpty());
        Assertions.assertEquals(2, participantResponses.size());
        Assertions.assertEquals(12, participantResponses.getFirst().id());
        Assertions.assertEquals("Иванов", participantResponses.getFirst().lastName());
        Assertions.assertEquals("Иван", participantResponses.getFirst().firstName());
        Assertions.assertEquals("Иванович", participantResponses.getFirst().middleName());
        Assertions.assertEquals(13, participantResponses.get(1).id());
        Assertions.assertEquals("Воробей", participantResponses.get(1).lastName());
        Assertions.assertEquals("Джек", participantResponses.get(1).firstName());
        Assertions.assertNull(participantResponses.get(1).middleName());

        Mockito
                .verify(participantClient, Mockito.times(1))
                .getAllByIds(Mockito.anySet());
    }
}
