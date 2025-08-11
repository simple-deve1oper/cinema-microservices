package dev.movie.repository;

import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@ActiveProfiles("test")
public class MovieRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private MovieRepository movieRepository;

    @Test
    void findDurationById_ok() {
        Optional<Integer> optionalDuration = movieRepository.findDurationById(1L);
        Assertions.assertTrue(optionalDuration.isPresent());
        Assertions.assertEquals(98, optionalDuration.get());
    }

    @Test
    void findDurationById_empty() {
        Optional<Integer> optionalDuration = movieRepository.findDurationById(1001L);
        Assertions.assertTrue(optionalDuration.isEmpty());
    }
}
