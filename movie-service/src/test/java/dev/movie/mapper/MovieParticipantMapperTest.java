package dev.movie.mapper;

import dev.library.domain.movie.dto.constant.AgeRating;
import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieParticipant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MovieParticipantMapperTest {
    final MovieParticipantMapper mapper = new MovieParticipantMapper();

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
        Long participantId = 567L;
        Position position = Position.ACTOR;

        MovieParticipant entity = mapper.toEntity(movie, participantId, position);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(movie, entity.getMovie());
        Assertions.assertEquals(participantId, entity.getParticipantId());
        Assertions.assertEquals(position, entity.getPosition());
    }
}
