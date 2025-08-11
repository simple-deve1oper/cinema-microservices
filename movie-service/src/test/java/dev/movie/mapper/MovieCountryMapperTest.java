package dev.movie.mapper;

import dev.library.domain.movie.dto.constant.AgeRating;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MovieCountryMapperTest {
    final MovieCountryMapper mapper = new MovieCountryMapper();

    @Test
    void toEntity() {
        Movie movie = Movie.builder()
                .id(145L)
                .name("Test")
                .description("Test")
                .duration(145)
                .year(2025)
                .ageRating(AgeRating.SIX)
                .rental(true)
                .build();
        String countryCode = "056";

        MovieCountry entity = mapper.toEntity(movie, countryCode);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(movie, entity.getMovie());
        Assertions.assertEquals(countryCode, entity.getCountryCode());
    }
}
