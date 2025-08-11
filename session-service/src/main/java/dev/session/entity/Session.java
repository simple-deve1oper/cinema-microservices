package dev.session.entity;

import dev.library.domain.session.dto.constant.MovieFormat;
import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Сущность для описания сеанса
 */
@Entity
@Table(name = "sessions")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true, exclude = {"places"})
@Builder
public class Session extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Идентификатор фильма
     */
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
    /**
     * Формат фильма
     */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "format", nullable = false)
    private MovieFormat movieFormat;
    /**
     * Зал
     */
    @Column(name = "hall", nullable = false)
    private Integer hall;
    /**
     * Дата и время
     */
    @Column(name = "date_time", nullable = false)
    private OffsetDateTime dateTime;
    /**
     * Доступность
     */
    @Column(name = "available", nullable = false)
    private Boolean available;

    /**
     * Список мест
     */
    @OneToMany(mappedBy = "session")
    private List<Place> places;
}
