package dev.dictionary.country.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность для описания страны
 */
@Entity
@Table(name = "countries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Builder
public class Country extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    /**
     * Код
     */
    @Column(name = "code", length = 3, unique = true, nullable = false)
    private String code;
    /**
     * Наименование
     */
    @Column(name = "name", length = 100, nullable = false)
    private String name;
}
