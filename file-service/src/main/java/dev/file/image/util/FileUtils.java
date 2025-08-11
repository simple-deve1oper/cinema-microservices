package dev.file.image.util;

import dev.library.core.exception.BadRequestException;
import dev.library.core.exception.EntityAlreadyExistsException;
import dev.library.core.exception.ServerException;
import dev.library.domain.file.dto.constant.ImageExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Objects;

/**
 * Вспомогательный класс для работы с файлами
 */
public class FileUtils {
    /**
     * Проверка существование директории и её создание, если не существует
     * @param directory - объект типа {@link Path}
     */
    public static void checkDirectoryAndCreateIfNotExists(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new ServerException("Ошибка создания директории %s".formatted(directory));
            }
        }
    }

    /**
     * Сохранение файла по переданному пути
     * @param file - объект типа {@link MultipartFile}
     * @param path - объект типа {@link Path}
     */
    public static void saveFile(MultipartFile file, Path path) {
        String fileName = file.getOriginalFilename();
        Path filePath = path.resolve(Objects.requireNonNull(fileName));
        try {
            if (Files.exists(filePath)) {
                throw new EntityAlreadyExistsException("Файл c именем %s уже существует".formatted(fileName));
            }
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ServerException("Ошибка сохранения файла %s: %s".formatted(fileName, e.getMessage()));
        }
    }

    /**
     * Удаление файла по переданному пути
     * @param file - объект типа {@link Path}
     */
    public static void deleteFile(Path file) {
        try {
            Files.delete(file);
        } catch (IOException e) {
            throw new ServerException("Путь %s не найден для удаления".formatted(file));
        }
    }

    /**
     * Проверка расширения загружаемого файла
     * @param image - объект типа {@link MultipartFile}
     */
    public static void checkExtensionImage(MultipartFile image) {
        String[] filenameArray = Objects.requireNonNull(image.getOriginalFilename()).split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        boolean result = Arrays.stream(ImageExtension.values()).anyMatch(ct -> ct.getValue().equals(extension));
        if (!result) {
            throw new BadRequestException("Загружаемый файл должен быть изображением в формате jpeg, jpg или png");
        }
    }
}
