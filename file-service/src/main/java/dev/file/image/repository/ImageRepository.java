package dev.file.image.repository;

import dev.file.image.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для сущности {@link Image}
 */
@Repository
public interface ImageRepository extends JpaRepository<Image, UUID> {
    /**
     * Получение всех записей об изображениях по идентификатору фильма
     * @param movieId - идентификатор фильма
     */
    List<Image> findAllByMovieId(Long movieId);

    /**
     * Получение записи об изображении по идентификатору фильма и порядковому номеру изображения
     * @param movieId - идентификатор фильма
     * @param number - порядковый номер изображения
     */
    Optional<Image> findByMovieIdAndNumber(Long movieId, Integer number);

    /**
     * Проверка на существование записи об изображении по наименованию файлу
     * @param fileName - наименование файла
     */
    boolean existsByFileName(String fileName);

    /**
     * Получение количества записей об изображении по идентификатору фильма
     * @param movieId - идентификатор фильма
     */
    int countByMovieId(Long movieId);

    /**
     * Обновление порядкового номера по наименованию файла и идентификатору фильма
     * @param movieId - идентификатор фильма
     * @param fileName - наименование файла
     * @param number - порядковый номер изображения
     */
    @Modifying
    @Query("UPDATE Image i SET i.number = :number WHERE i.fileName = :fileName AND i.movieId = :movieId")
    void editNumberByMovieId(Long movieId, String fileName, Integer number);
}
