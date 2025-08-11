package dev.file.image.service;

import dev.library.domain.file.dto.ImageRequest;
import dev.library.domain.file.dto.ImageResponse;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link dev.file.image.entity.Image}
 */
public interface ImageService {
    /**
     * Получение записей всех изображений
     */
    List<ImageResponse> getAll();

    /**
     * Получение записей всех изображений по идентификатору фильма
     * @param movieId - идентификатор фильма
     */
    List<ImageResponse> getAllByMovieId(Long movieId);

    /**
     * Получение файла изображения по идентификатору фильма и порядковому номеру изображения
     * @param movieId - идентификатор фильма
     * @param number - порядковый номер изображения
     */
    Resource getResourceByMovieIdAndNumber(Long movieId, Integer number);

    /**
     * Создание нового изображения для определенного фильма
     * @param movieId - идентификатор фильма
     * @param file - объект типа {@link MultipartFile}
     */
    void create(Long movieId, MultipartFile file);

    /**
     * Изменение порядкового номера в записях об изображениях
     * @param requests - список объектов типа {@link ImageRequest}
     */
    void updateImageNumbers(List<ImageRequest> requests);

    /**
     * Удаление записи и файла изображения по идентификатору
     * @param id - идентификатор
     */
    void deleteById(UUID id);
}
