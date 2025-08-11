package dev.file.image.service.impl;

import dev.file.image.service.FileService;
import dev.file.image.util.FileUtils;
import dev.library.core.exception.ServerException;
import dev.library.domain.file.dto.constant.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Сервис, реализующий интерфейс {@link FileService}
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    @Value("${directory.images}")
    private String directoryImages;
    @Value("${errors.file.path-exception}")
    private String filePathServerException;

    @Override
    public Resource get(String fileName, FileType fileType) {
        Path filePath = getFilePath(fileName, fileType);
        try {
            return new UrlResource(filePath.toUri());
        } catch (MalformedURLException e) {
            String errorMessage = filePathServerException.formatted(filePath);
            throw new ServerException(errorMessage);
        }
    }

    @Override
    public void save(MultipartFile file, FileType fileType) {
        Path directory = getDirectory(fileType);
        FileUtils.checkDirectoryAndCreateIfNotExists(directory);
        FileUtils.saveFile(file, directory);
    }

    @Override
    public void delete(String fileName, FileType fileType) {
        Path filePath = getFilePath(fileName, fileType);
        FileUtils.deleteFile(filePath);
    }

    /**
     * Получение объекта {@link Path} с путем до директории
     * @param fileType - перечисление типа {@link FileType}
     */
    private Path getDirectory(FileType fileType) {
        return switch (fileType) {
            case IMAGE -> Paths.get(directoryImages);
        };
    }

    /**
     * Получение объекта {@link Path} с путем до файла
     * @param fileName - путь до файла
     * @param fileType - перечисление типа {@link FileType}
     */
    private Path getFilePath(String fileName, FileType fileType) {
        Path directory = getDirectory(fileType);

        return directory.resolve(fileName);
    }
}
