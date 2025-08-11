package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Исключение для описания ошибок, которые связаны с ошибкой входных данных
 */
public class BadRequestException extends BaseException {
    public BadRequestException(String message) {
        super(
                HttpStatus.BAD_REQUEST,
                new ApiErrorResponse(
                        400,
                        message
                )
        );
    }

    public BadRequestException(String message, Map<String, String> fields) {
        super(
                HttpStatus.BAD_REQUEST,
                new ApiErrorResponse(HttpStatus.BAD_REQUEST.value(), message, fields)
        );
    }
}