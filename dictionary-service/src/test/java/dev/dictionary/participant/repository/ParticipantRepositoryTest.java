package dev.dictionary.participant.repository;

import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("test")
public class ParticipantRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ParticipantRepository participantRepository;

    @Test
    void findExistentIds_all() {
        List<Long> ids = List.of(1L, 4L, 10L);
        List<Long> countryIds = participantRepository.findExistentIds(ids);
        Assertions.assertEquals(3, countryIds.size());
        Assertions.assertEquals(1L, countryIds.get(0));
        Assertions.assertEquals(4L, countryIds.get(1));
        Assertions.assertEquals(10L, countryIds.get(2));
    }

    @Test
    void findExistentIds_some() {
        List<Long> ids = List.of(1L, 1024L, 999L);
        List<Long> countryIds = participantRepository.findExistentIds(ids);
        Assertions.assertEquals(1, countryIds.size());
        Assertions.assertEquals(1L, countryIds.getFirst());
    }

    @Test
    void findExistentIds_empty() {
        List<Long> ids = List.of(9997L, 1024L, 999L);
        List<Long> countryIds = participantRepository.findExistentIds(ids);
        Assertions.assertEquals(0, countryIds.size());
    }
}
