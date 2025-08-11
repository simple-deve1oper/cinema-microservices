package dev.movie.service.impl;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.dictionary.participant.client.ParticipantClient;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieParticipant;
import dev.movie.mapper.MovieParticipantMapper;
import dev.movie.repository.MovieParticipantRepository;
import dev.movie.service.MovieParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Сервис, реализующий интерфейс {@link MovieParticipantService}
 */
@Service
@RequiredArgsConstructor
public class MovieParticipantServiceImpl implements MovieParticipantService {
    private final MovieParticipantRepository repository;
    private final MovieParticipantMapper mapper;
    private final ParticipantClient client;

    @Value("${errors.participant.ids.not-found}")
    private String participantIdsNotFound;

    @Override
    @Transactional
    public List<MovieParticipant> create(Movie movie, Map<Position, Set<Long>> mapParticipants) {
        checkNonExistentIds(mapParticipants);

        return createEntities(movie, mapParticipants);
    }

    @Override
    @Transactional
    public void update(Movie movie, Map<Position, Set<Long>> mapParticipants) {
        checkNonExistentIds(mapParticipants);

        Set<Long> actorIds = mapParticipants.get(Position.ACTOR);
        Set<Long> directorIds = mapParticipants.get(Position.DIRECTOR);

        List<Long> currentActorIds = movie.getParticipants().stream()
                .filter(participant -> participant.getPosition() == Position.ACTOR)
                .map(MovieParticipant::getParticipantId)
                .toList();
        Set<Long> actorIdsForRemove = getIdsForRemove(currentActorIds, actorIds);
        if (!actorIdsForRemove.isEmpty()) {
            movie.getParticipants()
                    .removeIf(participant -> actorIdsForRemove.contains(participant.getParticipantId()) && participant.getPosition() == Position.ACTOR);
        }
        Set<Long> actorIdsForCreate = getIdsForCreate(currentActorIds, actorIds);

        List<Long> currentDirectorIds = movie.getParticipants().stream()
                .filter(participant -> participant.getPosition() == Position.DIRECTOR)
                .map(MovieParticipant::getParticipantId)
                .toList();
        Set<Long> directorIdsForRemove = getIdsForRemove(currentDirectorIds, directorIds);
        if (!directorIdsForRemove.isEmpty()) {
            movie.getParticipants()
                    .removeIf(participant -> directorIdsForRemove.contains(participant.getParticipantId()) && participant.getPosition() == Position.DIRECTOR);
        }
        Set<Long> directorIdsForCreate = getIdsForCreate(currentDirectorIds, directorIds);

        if (!actorIdsForCreate.isEmpty() || !directorIdsForCreate.isEmpty()) {
            List<MovieParticipant> participants = createEntities(movie, Map.of(Position.ACTOR, actorIdsForCreate, Position.DIRECTOR, directorIdsForCreate));
            movie.getParticipants().addAll(participants);
        }
    }

    @Override
    public List<ParticipantResponse> getParticipantResponseByMovieIdAndPosition(List<MovieParticipant> participants) {
        return client
                .getAllByIds(participants.stream().map(MovieParticipant::getParticipantId).collect(Collectors.toSet()));
    }

    /**
     * Проверка списка идентификаторов участников фильмов на не существующих участников фильма
     * @param mapParticipants - Map с идентификаторами участников фильмов
     */
    private void checkNonExistentIds(Map<Position, Set<Long>> mapParticipants) {
        Set<Long> actorIds = mapParticipants.get(Position.ACTOR);
        Set<Long> directorIds = mapParticipants.get(Position.DIRECTOR);
        Set<Long> participantIds = Stream.concat(actorIds.stream(), directorIds.stream()).collect(Collectors.toSet());

        List<Long> nonExistentIds = client.getNonExistentIds(participantIds);
        if (!nonExistentIds.isEmpty()) {
            String errorMessage = participantIdsNotFound
                    .formatted(nonExistentIds);
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Получение списка идентификаторов участников фильма для удаления
     * @param currentParticipantIds - текущий список идентификаторов участников фильма
     * @param participantIds - новый список идентификаторов участников фильма
     */
    private Set<Long> getIdsForRemove(List<Long> currentParticipantIds, Set<Long> participantIds) {
        return currentParticipantIds.stream()
                .filter(participantId -> !participantIds.contains(participantId))
                .collect(Collectors.toSet());
    }

    /**
     * Получение списка идентификаторов участников фильма для создания
     * @param currentParticipantIds - текущий список идентификаторов участников фильма
     * @param participantIds - новый список идентификаторов участников фильма
     */
    private Set<Long> getIdsForCreate(List<Long> currentParticipantIds, Set<Long> participantIds) {
        return participantIds.stream()
                .filter(participantId -> !currentParticipantIds.contains(participantId))
                .collect(Collectors.toSet());
    }

    /**
     * Создание новых записей об участников фильмов
     * @param movie - объект типа {@link Movie}
     * @param mapParticipants - Map с идентификаторами участников фильмов
     */
    private List<MovieParticipant> createEntities(Movie movie, Map<Position, Set<Long>> mapParticipants) {
        List<MovieParticipant> actors = mapParticipants.get(Position.ACTOR).stream()
                .map(id -> mapper.toEntity(movie, id, Position.ACTOR))
                .toList();
        List<MovieParticipant> directors = mapParticipants.get(Position.DIRECTOR).stream()
                .map(id -> mapper.toEntity(movie, id, Position.DIRECTOR))
                .toList();
        List<MovieParticipant> participants = Stream.concat(actors.stream(), directors.stream()).toList();

        return repository.saveAll(participants);
    }
}
