package dev.library.domain.movie.dto;

import dev.library.domain.movie.dto.constant.AgeRating;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.util.Set;

/**
 * DTO для создания/обновления данных по фильму
 * @param name - наименование
 * @param description - описание
 * @param duration - продолжительность
 * @param year - год выхода
 * @param ageRating - возрастной рейтинг
 * @param rental - прокат
 * @param genreIds - список идентификаторов жанров
 * @param countryCodes - список кодов стран
 * @param directorIds - список идентификаторов режиссёров
 * @param actorIds - список идентификаторов актёров
 */
@Schema(
        name = "ImageRequest",
        description = "DTO для создания/обновления данных по фильму"
)
public record MovieRequest(
        @Schema(name = "name", description = "Наименование")
        @NotBlank(message = "Наименование фильма не может быть пустым")
        @Length(max = 100, message = "Наименование фильма не может содержать более 100 символов")
        String name,
        @Schema(name = "description", description = "Описание")
        @NotBlank(message = "Описание фильма не может быть пустым")
        String description,
        @Schema(name = "duration", description = "Продолжительность")
        @NotNull(message = "Продолжительность фильма не может быть пустым")
        @Min(value = 25, message = "Минимальное значение продолжительности фильма 25")
        Integer duration,
        @Schema(name = "year", description = "Год выхода")
        @NotNull(message = "Год выхода фильма должен не может быть пустым")
        Integer year,
        @Schema(name = "ageRating", description = "Возрастной рейтинг")
        @NotNull(message = "Возрастной рейтинг не может быть пустым")
        AgeRating ageRating,
        @Schema(name = "rental", description = "Прокат")
        @NotNull(message = "Статус проката фильма не может быть пустым")
        Boolean rental,
        @Schema(name = "genreIds", description = "Список идентификаторов жанров")
        @NotEmpty(message = "Список идентификаторов жанров должен содержать хотя бы один элемент")
        Set<Long> genreIds,
        @Schema(name = "countryCodes", description = "Список кодов стран")
        @NotEmpty(message = "Список кодов стран должен содержать хотя бы один элемент")
        Set<String> countryCodes,
        @Schema(name = "directorIds", description = "Список идентификаторов режиссёров")
        @NotEmpty(message = "Список идентификаторов режиссёров должен содержать хотя бы один элемент")
        Set<Long> directorIds,
        @Schema(name = "actorIds", description = "Список идентификаторов актёров")
        @NotEmpty(message = "Список идентификаторов актёров должен содержать хотя бы один элемент")
        Set<Long> actorIds
) {}
