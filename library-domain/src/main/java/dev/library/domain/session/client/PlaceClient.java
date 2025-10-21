package dev.library.domain.session.client;

import dev.library.domain.session.dto.PlaceResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;
import java.util.Set;

/**
 * Клиент session-service для работы с местами сеансов
 */
public interface PlaceClient {
    /**
     * Получение записей мест по переданному списку идентификаторов
     * @param ids - список идентификаторов мест
     */
    @GetExchange("/search/ids")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodGetAllByIds")
    List<PlaceResponse> getAllByIds(@RequestParam(value = "values") Set<Long> ids);

    /**
     * Получение записи о месте по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodGetById")
    PlaceResponse getById(@PathVariable Long id);

    /**
     * Получение первого идентификатора места, который равен переданному идентификатору сеанса и доступности из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    @GetExchange("/search/session/{session-id}/ids")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodGetPlaceBySessionIdAndIdsAndAvailable")
    Long getPlaceBySessionIdAndIdsAndAvailable(@PathVariable("session-id") Long sessionId,
                                               @RequestParam(value = "values") Set<Long> ids,
                                               @RequestParam Boolean available);

    /**
     * Получение первого идентификатора места, который не равен переданному идентификатору сеанса из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    @GetExchange("/search/session-not-equals/{session-id}/ids")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodGetPlaceNotEqualsSessionBySessionIdAndIds")
    Long getPlaceNotEqualsSessionBySessionIdAndIds(@PathVariable("session-id") Long sessionId,
                                                   @RequestParam(value = "values") Set<Long> ids);

    /**
     * Обновление доступности мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    @HttpExchange(url = "/ids/update/available-places", method = "PATCH")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodUpdateAvailabilityAtPlaces")
    void updateAvailabilityAtPlaces(@RequestParam Long sessionId,
                                    @RequestParam Set<Long> ids,
                                    @RequestParam Boolean available);

    default List<PlaceResponse> fallbackMethodGetAllByIds(Set<Long> ids, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }

    default PlaceResponse fallbackMethodGetById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }

    default Long fallbackMethodGetPlaceBySessionIdAndIdsAndAvailable(Long sessionId, Set<Long> ids, Boolean available, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }

    default Long fallbackMethodGetPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Set<Long> ids, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }

    default void fallbackMethodUpdateAvailabilityAtPlaces(Long sessionId, Set<Long> ids, Boolean available, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }
}
