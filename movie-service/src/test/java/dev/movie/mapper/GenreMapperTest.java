package dev.movie.mapper;

import dev.library.domain.movie.dto.GenreResponse;
import dev.movie.entity.Genre;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;

public class GenreMapperTest {
    final GenreMapper mapper = new GenreMapper();

    @Test
    void toResponse() {
        Genre entity = Genre.builder()
                .id(123L)
                .name("Test")
                .movies(Collections.emptyList())
                .build();

        GenreResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getName(), response.name());
    }
}
