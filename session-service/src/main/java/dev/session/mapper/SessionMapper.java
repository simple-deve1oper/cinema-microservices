package dev.session.mapper;

import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.session.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link Session}
 */
@Component
@RequiredArgsConstructor
public class SessionMapper {
    /**
     * Преобразование данных в {@link SessionResponse}
     * @param session - объект типа {@link Session}
     */
    public SessionResponse toResponse(Session session) {
        return new SessionResponse(
                session.getId(),
                session.getMovieId(),
                session.getMovieFormat().getValue(),
                session.getHall(),
                session.getDateTime(),
                session.getAvailable()
        );
    }

    /**
     * Преобразование данных в {@link Session}
     * @param request - объект типа {@link SessionRequest}
     */
    public Session toEntity(SessionRequest request) {
        return Session.builder()
                .movieId(request.movieId())
                .movieFormat(request.movieFormat())
                .hall(request.hall())
                .dateTime(request.dateTime())
                .available(request.available())
                .build();
    }
}
