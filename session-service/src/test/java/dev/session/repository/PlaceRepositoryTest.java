package dev.session.repository;

import dev.library.test.config.AbstractRepositoryTest;
import dev.session.entity.Place;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@ActiveProfiles("test")
public class PlaceRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private PlaceRepository repository;

    @Test
    void findAllBySession_Id() {
        List<Place> places = repository.findAllBySession_Id(4L);
        Assertions.assertFalse(places.isEmpty());
        Assertions.assertEquals(25, places.size());

        places = repository.findAllBySession_Id(11999L);
        Assertions.assertTrue(places.isEmpty());
    }

    @Test
    void existsBySession_IdAndRowAndNumber() {
        boolean result = repository.existsBySession_IdAndRowAndNumber(4L, 2, 8);
        Assertions.assertTrue(result);

        result = repository.existsBySession_IdAndRowAndNumber(4L, 10, 101);
        Assertions.assertFalse(result);
    }

    @Test
    void findAllByIds() {
        List<Place> places = repository.findAllByIds(List.of(1L, 2L, 3L));
        Assertions.assertFalse(places.isEmpty());
        Assertions.assertEquals(3, places.size());
        Assertions.assertEquals(1L, places.get(0).getId());
        Assertions.assertEquals(2L, places.get(1).getId());
        Assertions.assertEquals(3L, places.get(2).getId());

        places = repository.findAllByIds(List.of(1015L, 2L, 308L));
        Assertions.assertFalse(places.isEmpty());
        Assertions.assertEquals(1, places.size());
        Assertions.assertEquals(2L, places.getFirst().getId());

        places = repository.findAllByIds(List.of(1022L, 889L, 308L));
        Assertions.assertTrue(places.isEmpty());
    }

    @Test
    void updateAvailable() {
        repository.updateAvailable(4L, Set.of(26L, 27L), false);
        List<Place> places = repository.findAllBySession_Id(4L);
        places = places.stream().filter(p -> p.getId().equals(26L) || p.getId().equals(27L)).toList();
        Assertions.assertFalse(places.get(0).getAvailable());
        Assertions.assertFalse(places.get(1).getAvailable());
    }

    @Test
    void findPlaceNotEqualsSessionBySessionIdAndIds() {
        Optional<Long> optionalLong = repository.findPlaceNotEqualsSessionBySessionIdAndIds(4L, Set.of(30L, 31L, 62L));
        Assertions.assertTrue(optionalLong.isPresent());
        Assertions.assertEquals(62L, optionalLong.get().longValue());

        optionalLong = repository.findPlaceNotEqualsSessionBySessionIdAndIds(4L, Set.of(30L, 31L, 32L));
        Assertions.assertTrue(optionalLong.isEmpty());
    }

    @Test
    void findPlaceBySessionIdAndAvailableAndIds() {
        Optional<Long> optionalLong = repository.findPlaceBySessionIdAndAvailableAndIds(4L, false, Set.of(34L, 35L, 36L));
        Assertions.assertTrue(optionalLong.isPresent());
        Assertions.assertEquals(36L, optionalLong.get().longValue());

        optionalLong = repository.findPlaceBySessionIdAndAvailableAndIds(8L, false, Set.of(134L, 235L, 336L));
        Assertions.assertTrue(optionalLong.isEmpty());
    }
}
