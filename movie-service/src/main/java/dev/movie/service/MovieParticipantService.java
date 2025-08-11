package dev.movie.service;

import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieParticipant;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link MovieParticipant}
 */
public interface MovieParticipantService {
    /**
     * Создание новых записей об участниках фильмов с фильмом
     * @param movie - объект типа {@link Movie}
     * @param mapParticipants - Map с идентификаторами участников фильмов
     */
    List<MovieParticipant> create(Movie movie, Map<Position, Set<Long>> mapParticipants);

    /**
     * Обновление существующих записей об участниках фильмов с фильмом
     * @param movie - объект типа {@link Movie}
     * @param mapParticipants - Map с идентификаторами участников фильмов
     */
    void update(Movie movie, Map<Position, Set<Long>> mapParticipants);

    /**
     * Получение списка объектов типа {@link ParticipantResponse} по списку объектов типа {@link MovieParticipant}
     * @param participants- список объектов типа {@link MovieParticipant}
     */
    List<ParticipantResponse> getParticipantResponseByMovieIdAndPosition(List<MovieParticipant> participants);
}
