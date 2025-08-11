package dev.file.image.mapper;

import dev.file.image.entity.Image;
import dev.library.domain.file.dto.ImageResponse;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link Image}
 */
@Component
public class ImageMapper {
    /**
     * Преобразование данных из {@link Image} в {@link ImageResponse}
     * @param image - объект типа Image
     */
    public ImageResponse toResponse(Image image) {
        return new ImageResponse(
                image.getId(),
                image.getMovieId(),
                image.getFileName(),
                image.getNumber()
        );
    }
}
