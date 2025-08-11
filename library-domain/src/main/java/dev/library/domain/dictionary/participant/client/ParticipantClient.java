package dev.library.domain.dictionary.participant.client;

import dev.library.domain.dictionary.participant.dto.ParticipantResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.Set;

/**
 * Клиент dictionary-service для работы с участниками фильмов
 */
public interface ParticipantClient {
    /**
     * Получение записей участников фильмов по переданным идентификаторам
     * @param values - список идентификаторов
     */
    @GetExchange("/search/ids")
    @CircuitBreaker(name = "dictionary", fallbackMethod = "fallbackMethodGetAllByIds")
    List<ParticipantResponse> getAllByIds(@RequestParam Set<Long> values);

    /**
     * Получение списка идентификаторов участников фильма, которые не принадлежат не одной существующей записи участников фильмов
     * @param ids - список идентификаторов
     */
    @GetExchange("/search/not-exists/ids")
    @CircuitBreaker(name = "dictionary", fallbackMethod = "fallbackMethodGetNonExistentIds")
    List<Long> getNonExistentIds(@RequestParam(name = "values") Set<Long> ids);

    default List<ParticipantResponse> fallbackMethodGetAllByIds(Set<Long> ids, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500),"Сервис справочника временно недоступен, повторите попытку позже!");
    }

    default List<Long> fallbackMethodGetNonExistentIds(Set<Long> ids, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис справочника временно недоступен, повторите попытку позже!");
    }
}
