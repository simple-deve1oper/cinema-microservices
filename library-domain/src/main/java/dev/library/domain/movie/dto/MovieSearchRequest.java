package dev.library.domain.movie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * DTO для фильтрации поиска данных по фильмам
 */
@Schema(
        name = "MovieSearchRequest",
        description = "DTO для фильтрации поиска данных по фильмам"
)
public class MovieSearchRequest {
    /**
     * Наименование
     */
    @Schema(name = "name", description = "Наименование")
    private String name;
    /**
     * Год выхода
     */
    @Schema(name = "year", description = "Год выхода")
    private Integer year;
    /**
     * Прокат
     */
    @Schema(name = "rental", description = "Прокат")
    private Boolean rental;

    public MovieSearchRequest() {}

    public MovieSearchRequest(String name, Integer year, Boolean rental) {
        this.name = name;
        this.year = year;
        this.rental = rental;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Boolean getRental() {
        return rental;
    }

    public void setRental(Boolean rental) {
        this.rental = rental;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MovieSearchRequest that = (MovieSearchRequest) o;
        return Objects.equals(name, that.name) && Objects.equals(year, that.year) && Objects.equals(rental, that.rental);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, rental);
    }

    @Override
    public String toString() {
        return "MovieSearchRequest{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", rental=" + rental +
                '}';
    }
}
