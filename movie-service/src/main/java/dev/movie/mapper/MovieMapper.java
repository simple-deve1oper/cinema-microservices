package dev.movie.mapper;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieRequest;
import dev.library.domain.movie.dto.MovieResponse;
import dev.movie.entity.Movie;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Класс для преобразования данных типа {@link Movie}
 */
@Component
public class MovieMapper {
    /**
     * Преобразование данных в {@link MovieResponse}
     * @param movie - объект типа {@link Movie}
     * @param genreResponses - список объектов типа {@link GenreResponse}
     * @param countryResponses - список объектов типа {@link CountryResponse}
     * @param directorResponses - список объектов типа {@link ParticipantResponse}
     * @param actorResponses - список объектов типа {@link ParticipantResponse}
     */
    public MovieResponse toResponse(Movie movie, List<GenreResponse> genreResponses, List<CountryResponse> countryResponses,
                                    List<ParticipantResponse> directorResponses, List<ParticipantResponse> actorResponses) {
        return new MovieResponse(
                movie.getId(),
                movie.getName(),
                movie.getDescription(),
                movie.getDuration(),
                movie.getYear(),
                movie.getAgeRating().getValue(),
                movie.getRental(),
                genreResponses,
                countryResponses,
                directorResponses,
                actorResponses
        );
    }

    /**
     * Преобразование данных в {@link Movie}
     * @param request - объект типа {@link MovieRequest}
     */
    public Movie toEntity(MovieRequest request) {
        return Movie.builder()
                .name(request.name())
                .description(request.description())
                .duration(request.duration())
                .year(request.year())
                .ageRating(request.ageRating())
                .rental(request.rental())
                .build();
    }
}
