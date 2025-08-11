package dev.movie.repository;

import dev.library.test.config.AbstractRepositoryTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Set;

@ActiveProfiles("test")
public class GenreRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private GenreRepository repository;

    @Test
    void findExistentIds_ok() {
        Set<Long> genreIds = Set.of(2L, 4L, 7L, 8L);

        List<Long> ids = repository.findExistentIds(genreIds);
        Assertions.assertNotNull(ids);
        Assertions.assertFalse(ids.isEmpty());
        Assertions.assertEquals(4, ids.size());
        Assertions.assertEquals(2L, ids.get(0));
        Assertions.assertEquals(4L, ids.get(1));
        Assertions.assertEquals(7L, ids.get(2));
        Assertions.assertEquals(8L, ids.get(3));
    }

    @Test
    void findExistentIds_some() {
        Set<Long> genreIds = Set.of(2L, 99L, 7L, 177L);

        List<Long> ids = repository.findExistentIds(genreIds);
        Assertions.assertNotNull(ids);
        Assertions.assertFalse(ids.isEmpty());
        Assertions.assertEquals(2, ids.size());
        Assertions.assertEquals(2L, ids.get(0));
        Assertions.assertEquals(7L, ids.get(1));
    }

    @Test
    void findExistentIds_empty() {
        Set<Long> genreIds = Set.of(101L, 99L, 711L, 177L);

        List<Long> ids = repository.findExistentIds(genreIds);
        Assertions.assertNotNull(ids);
        Assertions.assertTrue(ids.isEmpty());
    }
}
