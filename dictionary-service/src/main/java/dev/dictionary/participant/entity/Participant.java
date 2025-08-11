package dev.dictionary.participant.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность для описания участника фильма
 */
@Entity
@Table(name = "participants")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class Participant extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Фамилия
     */
    @Column(name = "last_name", length = 50, nullable = false)
    private String lastName;
    /**
     * Имя
     */
    @Column(name = "first_name", length = 50, nullable = false)
    private String firstName;
    /**
     * Отчество
     */
    @Column(name = "middle_name", length = 100)
    private String middleName;
}