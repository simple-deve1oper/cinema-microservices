package dev.library.domain.booking.client;

import dev.library.domain.booking.dto.BookingResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.service.annotation.GetExchange;

/**
 * Клиент booking-service для работы с бронированием
 */
public interface BookingClient {
    /**
     * Получение записи о бронировании по идентификатору
     * @param id - идентификатор
     */
    @GetExchange("/{id}")
    @CircuitBreaker(name = "booking", fallbackMethod = "fallbackMethodGetById")
    BookingResponse getById(@PathVariable Long id);

    /**
     * Проверка на существование бронирования по идентификатору и идентификатору пользователя
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    @GetExchange("/{id}/user")
    @CircuitBreaker(name = "booking", fallbackMethod = "fallbackMethodExistsByIdAndUserId")
    Boolean existsByIdAndUserId(@PathVariable Long id, @RequestParam String userId);

    default BookingResponse fallbackMethodGetById(Long id, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис бронирования временно недоступен, повторите попытку позже!");
    }

    default Boolean fallbackMethodExistsByIdAndUserId(Long id, String userId, Throwable throwable) {
        throw new HttpClientErrorException(HttpStatusCode.valueOf(500), "Сервис бронирования временно недоступен, повторите попытку позже!");
    }
}
