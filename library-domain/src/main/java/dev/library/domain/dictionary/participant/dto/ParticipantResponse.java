package dev.library.domain.dictionary.participant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO для получения данных об участнике фильма
 * @param id - идентификатор
 * @param lastName - фамилия
 * @param firstName - имя
 * @param middleName - отчество
 */
@Schema(
        name = "ParticipantResponse",
        description = "DTO для получения данных об участнике фильма"
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParticipantResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "lastName", description = "Фамилия")
        String lastName,
        @Schema(name = "firstName", description = "Имя")
        String firstName,
        @Schema(name = "middleName", description = "Отчество", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String middleName
) {
    public ParticipantResponse(Long id, String lastName, String firstName) {
        this(id, lastName, firstName, null);
    }
}
