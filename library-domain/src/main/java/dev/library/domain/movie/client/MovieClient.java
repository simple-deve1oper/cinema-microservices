package dev.library.domain.movie.client;

import dev.library.domain.movie.dto.MovieResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Клиент movie-service для работы с фильмами
 */
public interface MovieClient {
    /**
     * Получение записи о фильме по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}")
    @CircuitBreaker(name = "movie", fallbackMethod = "fallbackMethodGetById")
    MovieResponse getById(@PathVariable("id") Long id);

    /**
     * Проверка на существование записи о фильме по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/exists/{id}")
    @CircuitBreaker(name = "movie", fallbackMethod = "fallbackMethodExistsById")
    Boolean existsById(@PathVariable Long id);

    /**
     * Получение продолжительности фильма по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}/duration")
    @CircuitBreaker(name = "movie", fallbackMethod = "fallbackMethodGetDurationById")
    Integer getDurationById(@PathVariable("id") Long id);

    default MovieResponse fallbackMethodGetById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис фильмов временно недоступен, повторите попытку позже!");
    }

    default Boolean fallbackMethodExistsById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис фильмов временно недоступен, повторите попытку позже!");
    }

    default Integer fallbackMethodGetDurationById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис фильмов временно недоступен, повторите попытку позже!");
    }
}
