package dev.library.domain.dictionary.country.client;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

import java.util.List;
import java.util.Set;

/**
 * Клиент dictionary-service для работы со странами
 */
public interface CountryClient {
    /**
     * Получение записей стран по переданным кодам
     * @param codes - список кодов
     */
    @GetExchange("/search/codes")
    @CircuitBreaker(name = "dictionary", fallbackMethod = "fallbackMethodGetAllByCodes")
    List<CountryResponse> getAllByCodes(@RequestParam(name = "values") Set<String> codes);

    /**
     * Получение списка кодов стран, которые не принадлежат не одной существующей записи страны
     *
     * @param codes - список кодов
     */
    @GetExchange("/search/not-exists/codes")
    @CircuitBreaker(name = "dictionary", fallbackMethod = "fallbackMethodGetNonExistentCodes")
    List<String> getNonExistentCodes(@RequestParam(name = "values") Set<String> codes);

    default List<CountryResponse> fallbackMethodGetAllByCodes(Set<String> codes, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис справочника временно недоступен, повторите попытку позже!");
    }

    default List<String> fallbackMethodGetNonExistentCodes(Set<String> codes, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис справочника временно недоступен, повторите попытку позже!");
    }
}
