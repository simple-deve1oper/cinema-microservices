package dev.movie.service;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link MovieCountry}
 */
public interface MovieCountryService {
    /**
     * Создание новых записей о странах с фильмом
     * @param movie - объект типа {@link Movie}
     * @param countryCodes - список кодов стран
     */
    List<MovieCountry> create(Movie movie, Set<String> countryCodes);

    /**
     * Обновление существующих записей о странах с фильмом
     * @param movie - объект типа {@link Movie}
     * @param countryCodes - список кодов стран
     */
    void update(Movie movie, Set<String> countryCodes);

    /**
     * Получение списка объектов типа {@link CountryResponse} по списку объектов типа {@link MovieCountry}
     * @param countries - список объектов типа {@link MovieCountry}
     */
    List<CountryResponse> getCountryResponsesByMovieId(List<MovieCountry> countries);
}
