package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Исключение для описания ошибок сущностей, где не найдены данные
 */
public class EntityNotFoundException extends BaseException {
    public EntityNotFoundException(String message) {
        super(
                HttpStatus.NOT_FOUND,
                new ApiErrorResponse(HttpStatus.NOT_FOUND.value(), message)
        );
    }
}