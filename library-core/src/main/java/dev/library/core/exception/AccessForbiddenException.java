package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Исключение для описания ошибок, которые связаны с доступом
 */
public class AccessForbiddenException extends BaseException {
    public AccessForbiddenException(String message) {
        super(
                HttpStatus.FORBIDDEN,
                new ApiErrorResponse(HttpStatus.FORBIDDEN.value(), message)
        );
    }
}
