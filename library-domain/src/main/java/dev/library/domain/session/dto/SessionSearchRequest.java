package dev.library.domain.session.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.Objects;

/**
 * DTO для фильтрации поиска данных по сеансам
 */
@Schema(
        name = "SessionSearchRequest",
        description = "DTO для фильтрации поиска данных по сеансам"
)
public class SessionSearchRequest {
    /**
     * Идентификатор фильма
     */
    @Schema(name = "movieId", description = "Идентификатор фильма")
    private Long movieId;
    /**
     * Дата
     */
    @Schema(name = "date", description = "Дата")
    private LocalDate date;

    public SessionSearchRequest() {}

    public SessionSearchRequest(Long movieId, LocalDate date) {
        this.movieId = movieId;
        this.date = date;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SessionSearchRequest that = (SessionSearchRequest) o;
        return Objects.equals(movieId, that.movieId) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(movieId, date);
    }

    @Override
    public String toString() {
        return "SessionSearchRequest{" +
                "movieId=" + movieId +
                ", date=" + date +
                '}';
    }
}
