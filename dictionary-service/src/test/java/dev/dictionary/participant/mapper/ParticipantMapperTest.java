package dev.dictionary.participant.mapper;

import dev.dictionary.participant.entity.Participant;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ParticipantMapperTest {
    final ParticipantMapper mapper = new ParticipantMapper();

    @Test
    void toResponse() {
        Participant entity = Participant.builder()
                .id(125L)
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("Иванович")
                .build();

        ParticipantResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getLastName(), response.lastName());
        Assertions.assertEquals(entity.getFirstName(), response.firstName());
        Assertions.assertEquals(entity.getMiddleName(), response.middleName());
    }

    @Test
    void toResponse_withoutMiddleName() {
        Participant entity = Participant.builder()
                .id(125L)
                .lastName("Иванов")
                .firstName("Иван")
                .middleName(null)
                .build();

        ParticipantResponse response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getLastName(), response.lastName());
        Assertions.assertEquals(entity.getFirstName(), response.firstName());
        Assertions.assertNull(response.middleName());

        entity = Participant.builder()
                .id(125L)
                .lastName("Иванов")
                .firstName("Иван")
                .middleName("")
                .build();

        response = mapper.toResponse(entity);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(entity.getId(), response.id());
        Assertions.assertEquals(entity.getLastName(), response.lastName());
        Assertions.assertEquals(entity.getFirstName(), response.firstName());
        Assertions.assertNull(response.middleName());
    }
}
