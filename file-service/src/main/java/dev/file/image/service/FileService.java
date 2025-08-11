package dev.file.image.service;

import dev.library.domain.file.dto.constant.FileType;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * Интерфейс для описания абстрактных методов по работе с файлами
 */
public interface FileService {
    /**
     * Получение файла
     * @param fileName - наименование файла
     * @param fileType - перечисление типа {@link FileType}
     */
    Resource get(String fileName, FileType fileType);

    /**
     * Сохранение файла
     * @param file - объект типа {@link MultipartFile}
     * @param fileType - перечисление типа {@link FileType}
     */
    void save(MultipartFile file, FileType fileType);

    /**
     * Удаление файла
     * @param fileName - наименование файла
     * @param fileType - перечисление типа {@link FileType}
     */
    void delete(String fileName, FileType fileType);
}
