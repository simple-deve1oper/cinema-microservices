package dev.movie.repository;

import dev.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link Movie}
 */
@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    /**
     * Получение продолжительности фильма по идентификатору
     * @param id - идентификатор
     */
    @Query("SELECT m.duration FROM Movie m WHERE m.id = :id")
    Optional<Integer> findDurationById(Long id);
}
