package dev.gateway.exception.handler;

import dev.gateway.exception.dto.ApiErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class ExceptionRestHandler {
    private static final Logger log = LoggerFactory.getLogger(ExceptionRestHandler.class);

    @Value("${errors.fallback}")
    private String errorFallback;

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ApiErrorResponse> handleRestClientException(HttpServerErrorException exception) {
        log.error("Start HttpServerErrorException handler: {}", exception.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(exception.getStatusCode().value(), errorFallback);

        return ResponseEntity.status(exception.getStatusCode()).body(response);
    }
}
