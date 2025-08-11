package dev.movie.service;

import dev.movie.entity.Movie;
import dev.library.domain.movie.dto.MovieRequest;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.movie.dto.MovieSearchRequest;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Movie}
 */
public interface MovieService {
    /**
     * Получение записей всех фильмов
     * @param searchRequest - объект типа {@link MovieSearchRequest}
     */
    List<MovieResponse> getAll(MovieSearchRequest searchRequest);

    /**
     * Получение записи о фильме по идентификатору
     * @param id - идентификатор
     */
    MovieResponse getById(Long id);

    /**
     * Получение продолжительности фильма по идентификатору
     * @param id - идентификатор
     */
    Integer getDurationById(Long id);

    /**
     * Проверка на существование записи о фильме по идентификатору
     * @param id - идентификатор
     */
    boolean existsById(Long id);

    /**
     * Создание новой записи о фильме
     * @param request - объект типа {@link MovieRequest}
     */
    MovieResponse create(MovieRequest request);

    /**
     * Обновление существующей записи о фильме
     * @param id - идентификатор
     * @param request - объект типа {@link MovieRequest}
     */
    MovieResponse update(Long id, MovieRequest request);

    /**
     * Удаление записи о фильме по идентификатору
     * @param id - идентификатор
     */
    void deleteById(Long id);

    /**
     * Построение полного ответа о фильме
     * @param movie - объект типа {@link Movie}
     */
    MovieResponse buildResponse(Movie movie);
}
