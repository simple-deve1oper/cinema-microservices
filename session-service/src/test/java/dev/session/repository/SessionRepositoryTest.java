package dev.session.repository;

import dev.library.test.config.AbstractRepositoryTest;
import dev.session.entity.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;

@ActiveProfiles("test")
public class SessionRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private SessionRepository repository;

    @Test
    void existsByHallAndDateTime() {
        Session session = repository.findById(1L).orElseThrow();

        boolean exists = repository.existsByHallAndDateTime(session.getHall(), session.getDateTime());
        Assertions.assertTrue(exists);

        exists = repository.existsByHallAndDateTime(396, OffsetDateTime.now().minusDays(12));
        Assertions.assertFalse(exists);
    }

    @Test
    void updateAvailable() {
        repository.updateAvailable(1L, true);
        Session session = repository.findById(1L).orElseThrow();
        Assertions.assertTrue(session.getAvailable());
    }
}
