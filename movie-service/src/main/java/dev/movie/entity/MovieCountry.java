package dev.movie.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность для описания страны фильма
 */
@Entity
@Table(name = "movies_countries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class MovieCountry extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Фильм
     */
    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", nullable = false)
    private Movie movie;
    /**
     * Код страны
     */
    @Column(name = "country_code", length = 3, nullable = false)
    private String countryCode;
}