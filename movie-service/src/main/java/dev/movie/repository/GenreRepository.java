package dev.movie.repository;

import dev.movie.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для сущности {@link Genre}
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    /**
     * Получение идентификаторов существующих жанров по переданному списку идентификаторов жанров
     * @param ids - список идентификаторов
     */
    @Query(value = "SELECT g.id FROM Genre g WHERE g.id IN :ids")
    List<Long> findExistentIds(@Param("ids") Iterable<Long> ids);
}
