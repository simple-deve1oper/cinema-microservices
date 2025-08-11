package dev.movie.mapper;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import dev.library.domain.movie.dto.GenreResponse;
import dev.library.domain.movie.dto.MovieRequest;
import dev.library.domain.movie.dto.MovieResponse;
import dev.library.domain.movie.dto.constant.AgeRating;
import dev.movie.entity.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

public class MovieMapperTest {
    final MovieMapper mapper = new MovieMapper();

    @Test
    void toResponse() {
        Movie entity = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        List<GenreResponse> genreResponses = List.of(
                new GenreResponse(122L, "Боевик"),
                new GenreResponse(122L, "Тест")
        );
        List<CountryResponse> countryResponses = List.of(
                new CountryResponse(111L, "111", "Тест 111"),
                new CountryResponse(222L, "222", "Тест 222")
        );
        List<ParticipantResponse> directorResponses = List.of(
                new ParticipantResponse(9998L, "Петров", "Николай"),
                new ParticipantResponse(9999L, "Воробей", "Джек")
        );
        List<ParticipantResponse> actorResponses = List.of(
                new ParticipantResponse(10001L, "Рубцов", "Тимур"),
                new ParticipantResponse(10002L, "Крупов", "Анатолий", "Иванович")
        );

        MovieResponse response = mapper.toResponse(entity, genreResponses, countryResponses, directorResponses, actorResponses);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getName(), response.name());
        Assertions.assertEquals(entity.getDescription(), response.description());
        Assertions.assertEquals(entity.getDuration(), response.duration());
        Assertions.assertEquals(entity.getYear(), response.year());
        Assertions.assertEquals(entity.getAgeRating().getValue(), response.ageRating());
        Assertions.assertEquals(entity.getRental(), response.rental());
        Assertions.assertEquals(genreResponses, response.genres());
        Assertions.assertEquals(countryResponses, response.countries());
        Assertions.assertEquals(directorResponses, response.directors());
        Assertions.assertEquals(actorResponses, response.actors());
    }

    @Test
    void toEntity() {
        MovieRequest request = new MovieRequest("Test", "Test", 145, 2025,
                AgeRating.SIX, true,
                Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), Collections.emptySet());

        Movie entity = mapper.toEntity(request);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(request.name(), entity.getName());
        Assertions.assertEquals(request.description(), entity.getDescription());
        Assertions.assertEquals(request.duration(), entity.getDuration());
        Assertions.assertEquals(request.year(), entity.getYear());
        Assertions.assertEquals(request.ageRating(), entity.getAgeRating());
        Assertions.assertEquals(request.rental(), entity.getRental());
        Assertions.assertNull(entity.getGenres());
        Assertions.assertNull(entity.getCountries());
        Assertions.assertNull(entity.getParticipants());
    }
}
