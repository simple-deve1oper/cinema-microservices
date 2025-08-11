package dev.movie.mapper;

import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link MovieCountry}
 */
@Component
public class MovieCountryMapper {
    /**
     * Преобразование данных в {@link MovieCountry}
     * @param movie - объект типа {@link Movie}
     * @param countryCode - код страны
     */
    public MovieCountry toEntity(Movie movie, String countryCode) {
        return MovieCountry.builder()
                .movie(movie)
                .countryCode(countryCode)
                .build();
    }
}
