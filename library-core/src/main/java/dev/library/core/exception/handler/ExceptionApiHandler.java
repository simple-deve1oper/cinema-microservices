package dev.library.core.exception.handler;

import dev.library.core.exception.BaseException;
import dev.library.core.exception.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Обработчик исключений
 */
@RestControllerAdvice
public class ExceptionApiHandler {
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException exception) {
        return new ResponseEntity<>(exception.getApiError(), exception.getHttpStatus());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(HttpClientErrorException exception) {
        int statusCode = exception.getStatusCode().value();
        return new ResponseEntity<>(new ApiErrorResponse(statusCode, exception.getStatusText()), HttpStatus.valueOf(statusCode));
    }
}
