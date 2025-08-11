package dev.movie.service.impl;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.movie.dto.GenreResponse;
import dev.movie.entity.Genre;
import dev.movie.entity.Movie;
import dev.movie.mapper.GenreMapper;
import dev.movie.repository.GenreRepository;
import dev.movie.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link GenreService}
 */
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    private final GenreRepository repository;
    private final GenreMapper mapper;

    @Value("${errors.genre.ids.not-found}")
    private String errorGenreIdsNotFound;

    @Override
    public List<GenreResponse> getAll() {
        List<Genre> genres = repository.findAll();

        return genres.stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<Genre> addForMovie(Movie movie, Set<Long> genreIds) {
        checkNonExistentIds(genreIds);

        return addGenresForMovie(movie, genreIds);
    }

    @Override
    @Transactional
    public void updateForMovie(Movie movie, Set<Long> genreIds) {
        checkNonExistentIds(genreIds);
        List<Long> currentGenreIds = movie.getGenres().stream().map(Genre::getId).toList();
        Set<Long> genreIdsForRemove = getIdsForRemove(currentGenreIds, genreIds);
        if (!genreIdsForRemove.isEmpty()) {
            movie.getGenres().removeIf(genre -> genreIdsForRemove.contains(genre.getId()));
        }
        Set<Long> genreIdsForCreate = getIdsForCreate(currentGenreIds, genreIds);
        if (!genreIdsForCreate.isEmpty()) {
            List<Genre> genres = addGenresForMovie(movie, genreIdsForCreate);
            movie.getGenres().addAll(genres);
        }
    }

    @Override
    public List<GenreResponse> getGenreResponsesByMovie(Movie movie) {
        List<Genre> genres = movie.getGenres();

        return genres.stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Добавление записей о жанрах к фильму
     * @param movie - объект типа {@link Movie}
     * @param genreIds - список идентификаторов жанров
     */
    private List<Genre> addGenresForMovie(Movie movie, Set<Long> genreIds) {
        List<Genre> genres = repository.findAllById(genreIds);
        genres.forEach(genre -> genre.getMovies().add(movie));

        return repository.saveAll(genres);
    }

    /**
     * Проверка списка идентификаторов жанров на не существующие жанры
     * @param genreIds - список идентификаторов жанров
     */
    private void checkNonExistentIds(Set<Long> genreIds) {
        List<Long> existentIds = repository.findExistentIds(genreIds);
        List<Long> nonExistentIds = genreIds.stream()
                .filter(id -> !existentIds.contains(id))
                .toList();
        if (!nonExistentIds.isEmpty()) {
            String errorMessage = errorGenreIdsNotFound
                    .formatted(nonExistentIds);
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Получение списка идентификаторов жанров для удаления
     * @param currentGenreIds - текущий список идентификаторов жанров
     * @param genreIds - новый список идентификаторов жанров
     */
    private Set<Long> getIdsForRemove(List<Long> currentGenreIds, Set<Long> genreIds) {
        return currentGenreIds.stream()
                .filter(genreId -> !genreIds.contains(genreId))
                .collect(Collectors.toSet());
    }

    /**
     * Получение списка идентификаторов жанров для создания
     * @param currentGenreIds - текущий список идентификаторов жанров
     * @param genreIds - новый список идентификаторов жанров
     */
    private Set<Long> getIdsForCreate(List<Long> currentGenreIds, Set<Long> genreIds) {
        return genreIds.stream()
                .filter(genreId -> !currentGenreIds.contains(genreId))
                .collect(Collectors.toSet());
    }
}
