package dev.file.image.entity;

import dev.library.security.audit.entity.Auditable;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Абстрактный класс для описания сущности файла
 */
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public abstract class File extends Auditable {
    /**
     * Идентификатор
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;
    /**
     * Наименование файла
     */
    @Column(name = "file_name", nullable = false)
    private String fileName;
}
