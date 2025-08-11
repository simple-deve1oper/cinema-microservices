package dev.movie.mapper;

import dev.library.domain.movie.dto.GenreResponse;
import dev.movie.entity.Genre;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link Genre}
 */
@Component
public class GenreMapper {
    /**
     * Преобразование данных в {@link GenreResponse}
     * @param genre - объект типа {@link Genre}
     */
    public GenreResponse toResponse(Genre genre) {
        return new GenreResponse(
                genre.getId(),
                genre.getName()
        );
    }
}
