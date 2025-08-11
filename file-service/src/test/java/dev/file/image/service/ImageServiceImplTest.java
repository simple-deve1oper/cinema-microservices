package dev.file.image.service;

import dev.file.image.entity.Image;
import dev.file.image.mapper.ImageMapper;
import dev.file.image.repository.ImageRepository;
import dev.file.image.service.impl.FileServiceImpl;
import dev.file.image.service.impl.ImageServiceImpl;
import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.EntityNotFoundException;
import dev.library.domain.file.dto.ImageRequest;
import dev.library.domain.file.dto.ImageResponse;
import dev.library.domain.file.dto.constant.FileType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ImageServiceImplTest {
    final ImageRepository repository = Mockito.mock(ImageRepository.class);
    final ImageMapper mapper = new ImageMapper();
    final FileService fileService = Mockito.mock(FileServiceImpl.class);
    final ImageService service = new ImageServiceImpl(repository, mapper, fileService);

    Image imageTest1;
    Image imageTest2;
    Image imageTest3;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "errorsImageIdNotFound", "Запись об изображении с идентификатором %s не найдена");
        ReflectionTestUtils.setField(service, "errorsImageNameAlreadyExists", "Файл c именем %s уже существует");
        ReflectionTestUtils.setField(service, "errorsImageMovieIdBadRequest", "Обновление номеров изображений может происходить только по одному фильму за один запрос");
        ReflectionTestUtils.setField(service, "errorsImageMovieIdAndNumberNotFound", "Запись об изображении с идентификатором фильма %d и порядковым номером %d не найдена");
        ReflectionTestUtils.setField(service, "errorsImageNumberBadRequest", "В переданных объектах запроса номера не должны повторяться");
        ReflectionTestUtils.setField(service, "errorsImageNumberIsOrdinalBadRequest", "В переданных объектах запроса все номера должны быть порядковыми");

        imageTest1 = Image.builder()
                .id(UUID.randomUUID())
                .movieId(1L)
                .fileName("test.jpg")
                .number(1)
                .build();

        imageTest2 = Image.builder()
                .id(UUID.randomUUID())
                .movieId(2L)
                .fileName("test_image.jpg")
                .number(1)
                .build();

        imageTest3 = Image.builder()
                .id(UUID.randomUUID())
                .movieId(1L)
                .fileName("test3.jpg")
                .number(2)
                .build();
    }

    @Test
    void getAll_ok() {
        List<Image> images = List.of(imageTest1, imageTest2, imageTest3);

        Mockito
                .when(repository.findAll())
                .thenReturn(images);

        List<ImageResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(3, responses.size());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getAll_empty() {
        Mockito
                .when(repository.findAll())
                .thenReturn(Collections.emptyList());

        List<ImageResponse> responses = service.getAll();
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAll();
    }

    @Test
    void getByMovieId_ok() {
        List<Image> images = List.of(imageTest1, imageTest3);

        Mockito
                .when(repository.findAllByMovieId(Mockito.anyLong()))
                .thenReturn(images);

        List<ImageResponse> responses = service.getAllByMovieId(1L);
        Assertions.assertNotNull(responses);
        Assertions.assertFalse(responses.isEmpty());
        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(List.of(1L, 1L), responses.stream().map(ImageResponse::movieId).toList());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByMovieId(Mockito.anyLong());
    }

    @Test
    void getByMovieId_empty() {
        Mockito
                .when(repository.findAllByMovieId(Mockito.anyLong()))
                .thenReturn(Collections.emptyList());

        List<ImageResponse> responses = service.getAllByMovieId(99L);
        Assertions.assertNotNull(responses);
        Assertions.assertTrue(responses.isEmpty());

        Mockito
                .verify(repository, Mockito.times(1))
                .findAllByMovieId(Mockito.anyLong());
    }

    @Test
    void getByMovieIdAndNumber_ok() throws Exception {
        Path resourceDirectory = Paths.get("src","test","resources");
        Path filePath = resourceDirectory.resolve("test.jpg");

        Mockito
                .when(repository.findByMovieIdAndNumber(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(Optional.of(imageTest1));
        Mockito
                .when(fileService.get("test.jpg", FileType.IMAGE))
                .thenReturn(new UrlResource(filePath.toUri()));

        Resource resource = service.getResourceByMovieIdAndNumber(1L, 1);
        Assertions.assertNotNull(resource);
        Assertions.assertTrue(resource.exists());
        Assertions.assertTrue(resource.isFile());

        Mockito
                .verify(repository, Mockito.times(1))
                .findByMovieIdAndNumber(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void getByMovieIdAndNumber_notFound() {
        Mockito
                .when(repository.findByMovieIdAndNumber(Mockito.anyLong(), Mockito.anyInt()))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.getResourceByMovieIdAndNumber(8L, 3)
                );
        var expectedMessage = "Запись об изображении с идентификатором фильма 8 и порядковым номером 3 не найдена";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findByMovieIdAndNumber(Mockito.anyLong(), Mockito.anyInt());
    }

    @Test
    void create_ok() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test11.png",
                MediaType.IMAGE_PNG_VALUE,
                "Hello, World!".getBytes()
        );

        Mockito
                .when(repository.existsByFileName(Mockito.anyString()))
                .thenReturn(false);
        Mockito
                .when(repository.countByMovieId(Mockito.anyLong()))
                .thenReturn(2);
        Mockito
                .when(repository.save(Mockito.any(Image.class)))
                .thenReturn(Image.builder()
                        .id(UUID.randomUUID())
                        .movieId(1L)
                        .fileName("test11.png")
                        .number(3)
                        .build());
        Mockito
                .doNothing()
                .when(fileService)
                .save(Mockito.any(MultipartFile.class), Mockito.any(FileType.class));

        service.create(1L, file);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByFileName(Mockito.anyString());
        Mockito
                .verify(repository, Mockito.times(1))
                .countByMovieId(Mockito.anyLong());
        Mockito
                .verify(repository, Mockito.times(1))
                .save(Mockito.any(Image.class));
        Mockito
                .verify(fileService, Mockito.times(1))
                .save(Mockito.any(MultipartFile.class), Mockito.any(FileType.class));
    }

    @Test
    void create_entityAlreadyExistsException() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test12.png",
                MediaType.IMAGE_PNG_VALUE,
                "Hello, World!".getBytes()
        );

        Mockito
                .when(repository.existsByFileName(Mockito.anyString()))
                .thenReturn(true);

        EntityAlreadyExistsException exception = Assertions
                .assertThrows(
                        EntityAlreadyExistsException.class,
                        () -> service.create(67L, file)
                );
        var expectedMessage = "Файл c именем test12.png уже существует";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .existsByFileName(Mockito.anyString());
    }

    @Test
    void updateImageNumbers_ok() {
        Mockito
                .doNothing()
                .when(repository)
                .editNumberByMovieId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());

        List<ImageRequest> requests = List.of(
                new ImageRequest(45L, "abc.png", 1),
                new ImageRequest(45L, "efg.png", 2)
        );
        service.updateImageNumbers(requests);

        Mockito
                .verify(repository, Mockito.times(2))
                .editNumberByMovieId(Mockito.anyLong(), Mockito.anyString(), Mockito.anyInt());
    }

    @Test
    void updateImageNumbers_badRequest() {
        List<ImageRequest> requests = List.of(
                new ImageRequest(58L, "ghi.png", 78),
                new ImageRequest(58L, "jkl.png", 10)
        );
        BadRequestException exception = Assertions
                .assertThrows(
                        BadRequestException.class,
                        () -> service.updateImageNumbers(requests)
                );
        var expectedMessage = "В переданных объектах запроса все номера должны быть порядковыми";
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void deleteById_ok() {
        Mockito
                .when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.of(imageTest2));
        Mockito
                .doNothing()
                .when(fileService)
                .delete(Mockito.anyString(), Mockito.any(FileType.class));
        Mockito
                .doNothing()
                .when(repository)
                .deleteById(Mockito.any(UUID.class));

        service.deleteById(imageTest2.getId());

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.any(UUID.class));
        Mockito
                .verify(fileService, Mockito.times(1))
                .delete(Mockito.anyString(), Mockito.any(FileType.class));
        Mockito
                .verify(repository, Mockito.times(1))
                .deleteById(Mockito.any(UUID.class));
    }

    @Test
    void deleteById_notFound() {
        Mockito
                .when(repository.findById(Mockito.any(UUID.class)))
                .thenReturn(Optional.empty());

        UUID id = UUID.randomUUID();
        EntityNotFoundException exception = Assertions
                .assertThrows(
                        EntityNotFoundException.class,
                        () -> service.deleteById(id)
                );
        var expectedMessage = "Запись об изображении с идентификатором %s не найдена".formatted(id);
        var actualMessage = exception.getApiError().message();
        Assertions.assertEquals(expectedMessage, actualMessage);

        Mockito
                .verify(repository, Mockito.times(1))
                .findById(Mockito.any(UUID.class));
    }
}
