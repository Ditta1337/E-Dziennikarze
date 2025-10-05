package com.edziennikarze.gradebook.exception;

import io.r2dbc.spi.R2dbcException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PropertyParseException.class)
    public ResponseEntity<ErrorResponse> handlePropertyParse(PropertyParseException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(CollisionException.class)
    public ResponseEntity<ErrorResponse> handleCollision(CollisionException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AuthorizationDeniedException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied. You do not have permission to perform this action.",
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleServerWebInput(ServerWebInputException ex, ServerWebExchange exchange) {
        String message = ex.getReason() != null ? ex.getReason() : "Invalid request input.";
        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler({R2dbcException.class, DataAccessException.class})
    public ResponseEntity<ErrorResponse> handleDatabaseExceptions(Exception ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A database error occurred. Please try again later.",
                exchange.getRequest().getPath().value()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, ServerWebExchange exchange) {
        return createErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred.",
                exchange.getRequest().getPath().value()
        );
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse body = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return new ResponseEntity<>(body, status);
    }
}
