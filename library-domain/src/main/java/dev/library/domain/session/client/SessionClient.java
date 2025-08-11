package dev.library.domain.session.client;

import dev.library.domain.session.dto.SessionResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Клиент session-service для работы с сеансами
 */
public interface SessionClient {
    /**
     * Получение записи о сеансе по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}")
    @CircuitBreaker(name = "session", fallbackMethod = "fallbackMethodGetById")
    SessionResponse getById(@PathVariable Long id);

    default SessionResponse fallbackMethodGetById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис сеансов временно недоступен, повторите попытку позже!");
    }
}
