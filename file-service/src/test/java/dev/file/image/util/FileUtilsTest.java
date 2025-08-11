package dev.file.image.util;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FileUtilsTest {
    static FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());

    @Test
    @Order(1)
    void checkDirectoryAndCreateIfNotExists() {
        Path path = fileSystem.getPath("/files/images");
        FileUtils.checkDirectoryAndCreateIfNotExists(path);
        boolean result = Files.exists(path);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(2)
    void saveFile() {
        Path path = fileSystem.getPath("/files/images");
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes()
        );
        FileUtils.saveFile(file, path);
        Path filePath = path.resolve("test.png");
        boolean result = Files.exists(filePath);
        Assertions.assertTrue(result);
    }

    @Test
    @Order(3)
    void deleteFile() {
        Path path = fileSystem.getPath("/files/images");
        Path filePath = path.resolve("test.png");
        boolean result = Files.exists(filePath);
        Assertions.assertTrue(result);
        FileUtils.deleteFile(filePath);
        result = Files.exists(filePath);
        Assertions.assertFalse(result);
    }

    @Test
    @Order(4)
    void checkExtensionImage() {
        MockMultipartFile file
                = new MockMultipartFile(
                "file",
                "test.png",
                MediaType.IMAGE_PNG_VALUE,
                "".getBytes()
        );
        FileUtils.checkExtensionImage(file);
    }
}
