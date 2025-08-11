package dev.library.core.exception;

import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;

/**
 * Базовый абстрактный класс для создания своих собственных исключений
 */
public abstract class BaseException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ApiErrorResponse apiError;

    public BaseException(HttpStatus httpStatus, ApiErrorResponse apiError) {
        this.httpStatus = httpStatus;
        this.apiError = apiError;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ApiErrorResponse getApiError() {
        return apiError;
    }
}