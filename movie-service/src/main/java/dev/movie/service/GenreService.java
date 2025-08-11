package dev.movie.service;

import dev.library.domain.movie.dto.GenreResponse;
import dev.movie.entity.Genre;
import dev.movie.entity.Movie;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Genre}
 */
public interface GenreService {
    /**
     * Получение всех записей жанров
     */
    List<GenreResponse> getAll();

    /**
     * Добавление новых записей о жанре к фильму
     * @param movie - объект типа {@link Movie}
     * @param genreIds - список идентификаторов жанров
     */
    List<Genre> addForMovie(Movie movie, Set<Long> genreIds);

    /**
     * Обновление существующих записей о жанрах к фильму
     * @param movie - объект типа {@link Movie}
     * @param genreIds - список идентификаторов жанров
     */
    void updateForMovie(Movie movie, Set<Long> genreIds);

    /**
     * Получение списка объектов типа {@link GenreResponse} по объекту типа {@link Movie}
     * @param movie - объект типа {@link Movie}
     */
    List<GenreResponse> getGenreResponsesByMovie(Movie movie);
}
