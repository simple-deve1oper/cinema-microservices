package dev.movie.mapper;

import dev.library.domain.movie.dto.constant.Position;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieParticipant;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link MovieParticipant}
 */
@Component
public class MovieParticipantMapper {
    /**
     * Преобразование данных в {@link MovieParticipant}
     * @param movie - - объект типа {@link Movie}
     * @param participantId - идентификатор участника
     * @param position - перечисление типа {@link Position}
     */
    public MovieParticipant toEntity(Movie movie, Long participantId, Position position) {
        return MovieParticipant.builder()
                .movie(movie)
                .participantId(participantId)
                .position(position)
                .build();
    }
}
