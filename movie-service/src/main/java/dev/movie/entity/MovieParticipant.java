package dev.movie.entity;

import dev.library.domain.movie.dto.constant.Position;
import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

/**
 * Сущность для описания участника фильма
 */
@Entity
@Table(name = "movies_participants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class MovieParticipant extends Auditable {
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
     * Идентификатор участника фильма
     */
    @Column(name = "participant_id", nullable = false)
    private Long participantId;
    /**
     * Позиция
     */
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "position", nullable = false)
    private Position position;
}