package dev.file.image.service;

import dev.file.image.service.impl.FileServiceImpl;
import dev.library.domain.file.dto.constant.FileType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.CleanupMode;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileServiceImplTest {
    final FileService service = new FileServiceImpl();

    @TempDir(cleanup = CleanupMode.ALWAYS)
    static Path tempDir;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "directoryImages", tempDir.toString());
        ReflectionTestUtils.setField(service, "filePathServerException", "Ошибка получения доступа к ресурсу по пути test");
    }

    @Test
    @Order(2)
    void get() {
        Resource resource = service.get("test.png", FileType.IMAGE);
        Assertions.assertNotNull(resource);
        Assertions.assertTrue(resource.exists());
    }

    @Test
    @Order(1)
    void save() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes()
        );
        service.save(file, FileType.IMAGE);
        Path filePath = tempDir.resolve("test.png");
        boolean result = Files.exists(filePath);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(3)
    void delete() {
        service.delete("test.png", FileType.IMAGE);
        Path filePath = tempDir.resolve("test.png");
        boolean result = Files.exists(filePath);
        Assertions.assertFalse(result);
    }
}
