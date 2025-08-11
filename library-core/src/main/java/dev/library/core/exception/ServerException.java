package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Исключение для описания ошибок сущностей, которые связаны с ошибкой сервера
 */
public class ServerException extends BaseException {
    public ServerException(String message) {
        super(
                HttpStatus.INTERNAL_SERVER_ERROR,
                new ApiErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), message)
        );
    }
}