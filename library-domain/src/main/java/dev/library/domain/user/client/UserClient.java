package dev.library.domain.user.client;

import dev.library.domain.user.dto.UserResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Клиент user-service для работы с пользователями
 */
public interface UserClient {
    /**
     * Получение записи о пользователе по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}")
    @CircuitBreaker(name = "user", fallbackMethod = "fallbackMethodGetById")
    UserResponse getById(@PathVariable("id") String id);

    default UserResponse fallbackMethodGetById(String id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис пользователей временно недоступен, повторите попытку позже!");
    }
}
