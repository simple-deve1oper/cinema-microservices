package dev.file.image.repository;

import dev.file.image.entity.Image;
import dev.library.test.config.AbstractRepositoryTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private EntityManager entityManager;

    @Test
    @Order(1)
    void findAllByMovieId_all() {
        List<Image> images = imageRepository.findAllByMovieId(1L);
        Assertions.assertEquals(1, images.size());
        Assertions.assertEquals(1L, images.getFirst().getMovieId());
    }

    @Test
    @Order(2)
    void findAllByMovieId_empty() {
        List<Image> images = imageRepository.findAllByMovieId(1001L);
        Assertions.assertEquals(0, images.size());
    }

    @Test
    @Order(3)
    void findByMovieIdAndNumber_ok() {
        Optional<Image> optionalImage = imageRepository.findByMovieIdAndNumber(2L, 1);
        Assertions.assertTrue(optionalImage.isPresent());

        Image image = optionalImage.get();
        Assertions.assertNotNull(image.getId());
        Assertions.assertEquals("paddington-in-peru-poster.jpg", image.getFileName());
        Assertions.assertEquals(2L, image.getMovieId());
        Assertions.assertEquals(1, image.getNumber());
    }

    @Test
    @Order(4)
    void findByMovieIdAndNumber_empty() {
        Optional<Image> optionalImage = imageRepository.findByMovieIdAndNumber(105L, 101);
        Assertions.assertTrue(optionalImage.isEmpty());
    }

    @Test
    @Order(5)
    void existsByFileName() {
        boolean result = imageRepository.existsByFileName("black-cab-poster.jpeg");
        Assertions.assertTrue(result);
        result = imageRepository.existsByFileName("test.png");
        Assertions.assertFalse(result);
    }

    @Test
    @Order(6)
    void countByMovieId() {
        int count = imageRepository.countByMovieId(1L);
        Assertions.assertEquals(1, count);
    }

    @Test
    @Order(7)
    void editNumberByMovieId() {
        List<Image> images = imageRepository.findAllByMovieId(2L);
        Assertions.assertEquals(1, images.size());
        Assertions.assertEquals(2L, images.getFirst().getMovieId());
        Assertions.assertEquals(1, images.getFirst().getNumber());
        Assertions.assertEquals("paddington-in-peru-poster.jpg", images.getFirst().getFileName());

        imageRepository.editNumberByMovieId(2L, "paddington-in-peru-poster.jpg", 5);
        entityManager.clear();

        images = imageRepository.findAllByMovieId(2L);
        Assertions.assertEquals(1, images.size());
        Assertions.assertEquals(2L, images.getFirst().getMovieId());
        Assertions.assertEquals(5, images.getFirst().getNumber());
        Assertions.assertEquals("paddington-in-peru-poster.jpg", images.getFirst().getFileName());
    }
}
