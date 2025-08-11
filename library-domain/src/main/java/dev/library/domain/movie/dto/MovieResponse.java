package dev.library.domain.movie.dto;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * DTO для получения данных о фильме
 * @param id - идентификатор
 * @param name - наименование
 * @param description - описание
 * @param duration - продолжительность
 * @param year - год выхода
 * @param ageRating - возрастной рейтинг
 * @param rental - прокат
 * @param genres - список объектов типа {@link GenreResponse}
 * @param countries - список объектов типа {@link CountryResponse}
 * @param directors - список объектов типа {@link ParticipantResponse}
 * @param actors - список объектов типа {@link ParticipantResponse}
 */
@Schema(
        name = "GenreResponse",
        description = "DTO для получения данных о фильме"
)
public record MovieResponse(
        @Schema(name = "id", description = "Идентификатор")
        Long id,
        @Schema(name = "name", description = "Наименование")
        String name,
        @Schema(name = "description", description = "Описание")
        String description,
        @Schema(name = "duration", description = "Продолжительность")
        Integer duration,
        @Schema(name = "year", description = "Год выхода")
        Integer year,
        @Schema(name = "ageRating", description = "Возрастной рейтинг")
        String ageRating,
        @Schema(name = "rental", description = "Прокат")
        Boolean rental,
        @Schema(name = "genres", description = "Список жанров")
        List<GenreResponse> genres,
        @Schema(name = "countries", description = "Список стран")
        List<CountryResponse> countries,
        @Schema(name = "directors", description = "Список режиссёров")
        List<ParticipantResponse> directors,
        @Schema(name = "actors", description = "Список актёров")
        List<ParticipantResponse> actors
) {}
