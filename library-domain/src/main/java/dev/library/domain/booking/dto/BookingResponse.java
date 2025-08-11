package dev.library.domain.booking.dto;

import dev.library.domain.session.dto.PlaceResponse;
import dev.library.domain.session.dto.SessionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO для получения данных о бронировании
 * @param id - идентификатор
 * @param userId - идентификатор пользователя
 * @param session - объект типа {@link SessionResponse}
 * @param places - список объектов типа {@link PlaceResponse}
 * @param status - статус бронирования
 * @param createdDate - дата и время создания
 * @param updatedDate - дата и время обновления
 */
@Schema(
        name = "BookingResponse",
        description = "DTO для получения данных о бронировании"
)
public record BookingResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "userId", description = "Идентификатор пользователя")
        String userId,
        @Schema(name = "session", description = "Сеанс")
        SessionResponse session,
        @Schema(name = "places", description = "Список мест")
        List<PlaceResponse> places,
        @Schema(name = "status", description = "Статус бронирования")
        String status,
        @Schema(name = "createdDate", description = "Дата создания")
        OffsetDateTime createdDate,
        @Schema(name = "updatedDate", description = "Дата обновления")
        OffsetDateTime updatedDate
) {}
