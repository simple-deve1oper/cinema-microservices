package dev.library.core.exception.handler;

import dev.library.core.exception.BaseException;
import dev.library.core.exception.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(ExceptionApiHandler.class);

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiErrorResponse> handleBaseException(BaseException exception) {
        log.error(exception.getMessage());
        return new ResponseEntity<>(exception.getApiError(), exception.getHttpStatus());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntimeException(HttpClientErrorException exception) {
        log.error(exception.getMessage());
        int statusCode = exception.getStatusCode().value();
        return new ResponseEntity<>(new ApiErrorResponse(statusCode, exception.getStatusText()), HttpStatus.valueOf(statusCode));
    }
}
