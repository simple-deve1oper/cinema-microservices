package dev.movie.repository;

import dev.movie.entity.MovieCountry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий для сущности {@link MovieCountry}
 */
@Repository
public interface MovieCountryRepository extends JpaRepository<MovieCountry, Long> {}