package dev.movie.entity;

import dev.library.domain.movie.dto.constant.AgeRating;
import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.List;

/**
 * Сущность для описания фильма
 */
@Entity
@Table(name = "movies")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"genres", "countries", "participants"})
@Builder
public class Movie extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Наименование
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;
    /**
     * Описание
     */
    @Column(name = "description", nullable = false)
    private String description;
    /**
     * Продолжительность
     */
    @Column(name = "duration", nullable = false)
    private Integer duration;
    /**
     * Год выхода
     */
    @Column(name = "year", nullable = false)
    private Integer year;
    /**
     * Возрастной рейтинг
     */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "rating", nullable = false)
    private AgeRating ageRating;
    /**
     * Прокат
     */
    @Column(name = "rental", nullable = false)
    private Boolean rental;
    /**
     * Список жанров
     */
    @ManyToMany
    @JoinTable(
            name = "movies_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;
    /**
     * Список стран
     */
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCountry> countries;
    /**
     * Список участников фильмов
     */
    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieParticipant> participants;
}