package dev.file.image.mapper;

import dev.file.image.entity.Image;
import dev.library.domain.file.dto.ImageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ImageMapperTest {
    final ImageMapper mapper = new ImageMapper();

    @Test
    void toResponse() {
        Image entity = Image.builder()
                .id(UUID.randomUUID())
                .fileName("test.jpg")
                .movieId(45L)
                .number(23)
                .build();

        ImageResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.id());
        Assertions.assertEquals(entity.getFileName(), response.fileName());
        Assertions.assertEquals(entity.getMovieId(), response.movieId());
        Assertions.assertEquals(entity.getNumber(), response.number());
    }
}
