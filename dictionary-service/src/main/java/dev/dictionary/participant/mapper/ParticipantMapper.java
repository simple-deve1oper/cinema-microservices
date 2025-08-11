package dev.dictionary.participant.mapper;

import dev.dictionary.participant.entity.Participant;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link Participant}
 */
@Component
public class ParticipantMapper {
    /**
     * Преобразование данных из {@link Participant} в {@link ParticipantResponse}
     * @param participant - объект типа Participant
     */
    public ParticipantResponse toResponse(Participant participant) {
        if (participant.getMiddleName() == null || participant.getMiddleName().isBlank()) {
            return new ParticipantResponse(
                    participant.getId(),
                    participant.getLastName(),
                    participant.getFirstName()
            );
        } else {
            return new ParticipantResponse(
                    participant.getId(),
                    participant.getLastName(),
                    participant.getFirstName(),
                    participant.getMiddleName()
            );
        }
    }
}
