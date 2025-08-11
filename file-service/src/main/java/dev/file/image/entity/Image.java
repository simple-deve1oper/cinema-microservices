package dev.file.image.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Сущность для описания изображения
 */
@Entity
@Table(name = "images")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Image extends File {
    /**
     * Идентификатор фильма
     */
    @Column(name = "movie_id", nullable = false)
    private Long movieId;
    /**
     * Порядковый номер изображения
     */
    @Column(name = "number", nullable = false)
    private Integer number;

    @Builder
    public Image(UUID id, String fileName, Long movieId, Integer number) {
        super(id, fileName);
        this.movieId = movieId;
        this.number = number;
    }
}