package dev.session.mapper;

import dev.library.domain.session.dto.PlaceRequest;
import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.constant.MovieFormat;
import dev.session.entity.Place;
import dev.session.entity.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PlaceMapperTest {
    final PlaceMapper mapper = new PlaceMapper();

    @Test
    void toResponse() {
        Session session = Session.builder()
                .id(4L)
                .movieId(4L)
                .movieFormat(MovieFormat.THREE_D)
                .hall(3)
                .dateTime(OffsetDateTime.of(LocalDate.of(2025, 8, 5), LocalTime.of(12, 15), ZoneOffset.UTC))
                .available(false)
                .build();
        Place entity = Place.builder()
                .id(12L)
                .session(session)
                .row(5)
                .number(22)
                .price(BigDecimal.valueOf(400.99))
                .available(false)
                .build();

        PlaceResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getSession().getId(), response.sessionId());
        Assertions.assertEquals(entity.getRow(), response.row());
        Assertions.assertEquals(entity.getNumber(), response.number());
        Assertions.assertEquals(entity.getPrice().toString(), response.price());
        Assertions.assertEquals(entity.getAvailable(), response.available());
    }

    @Test
    void toEntity() {
        PlaceRequest request = new PlaceRequest(1L, 1, 1, BigDecimal.valueOf(100), false);

        Place entity = mapper.toEntity(request);
        Assertions.assertNotNull(entity);
        Assertions.assertNull(entity.getSession());
        Assertions.assertEquals(request.row(), entity.getRow());
        Assertions.assertEquals(request.number(), entity.getNumber());
        Assertions.assertEquals(request.price(), entity.getPrice());
        Assertions.assertEquals(request.available(), entity.getAvailable());
    }
}
