package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Исключение для описания ошибок, которые связаны с ошибкой уже существующим данными
 */
public class EntityAlreadyExistsException extends BaseException {
    public EntityAlreadyExistsException(String message) {
        super(
                HttpStatus.CONFLICT,
                new ApiErrorResponse(HttpStatus.CONFLICT.value(), message)
        );
    }
}