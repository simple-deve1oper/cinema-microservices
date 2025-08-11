package dev.movie.repository;

import dev.movie.entity.MovieParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для сущности {@link MovieParticipant}
 */
@Repository
public interface MovieParticipantRepository extends JpaRepository<MovieParticipant, Long> { }