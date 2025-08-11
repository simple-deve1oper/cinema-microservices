package dev.session.mapper;

import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.session.entity.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class SessionMapperTest {
    final SessionMapper mapper = new SessionMapper();

    @Test
    void toResponse() {
        Session entity = Session.builder()
                .id(4L)
                .movieId(4L)
                .movieFormat(MovieFormat.THREE_D)
                .hall(3)
                .dateTime(OffsetDateTime.of(LocalDate.of(2025, 8, 5), LocalTime.of(12, 15), ZoneOffset.UTC))
                .available(false)
                .build();

        SessionResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getMovieId(), response.movieId());
        Assertions.assertEquals(entity.getMovieFormat().getValue(), response.movieFormat());
        Assertions.assertEquals(entity.getHall(), response.hall());
        Assertions.assertEquals(entity.getDateTime(), response.dateTime());
        Assertions.assertEquals(entity.getAvailable(), response.available());
    }

    @Test
    void toEntity() {
        SessionRequest request = new SessionRequest(4L, MovieFormat.THREE_D, 3, OffsetDateTime.now(), true);

        Session entity = mapper.toEntity(request);
        Assertions.assertNotNull(entity);
        Assertions.assertEquals(request.movieId(), entity.getMovieId());
        Assertions.assertEquals(request.movieFormat(), entity.getMovieFormat());
        Assertions.assertEquals(request.hall(), entity.getHall());
        Assertions.assertEquals(request.dateTime(), entity.getDateTime());
        Assertions.assertEquals(request.available(), entity.getAvailable());
    }
}
