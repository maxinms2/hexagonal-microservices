package com.microservices.order.infrastructure.adapter.input.rest;

import com.microservices.order.domain.exception.InvalidOrderStateException;
import com.microservices.order.domain.exception.OrderNotFoundException;
import com.microservices.order.domain.exception.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 🚨 GLOBAL EXCEPTION HANDLER
 * 
 * Maneja todas las excepciones de la aplicación de forma centralizada.
 * 
 * ¿Por qué?
 * 1. Respuestas consistentes de error
 * 2. No repetir código de manejo de errores
 * 3. Logging centralizado
 * 4. Ocultar detalles técnicos al cliente
 * 
 * @RestControllerAdvice: Captura excepciones de todos los @RestController
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja OrderNotFoundException
     * HTTP 404 Not Found
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(OrderNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(),
            "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Maneja UserNotFoundException (comunicación inter-microservicios)
     * HTTP 422 Unprocessable Entity
     * 
     * Se devuelve 422 porque técnicamente la entidad (usuario) no existe,
     * lo que hace que la orden no se pueda procesar.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "User Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
    /**
     * Maneja errores de validación (@Valid)
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String field = ((FieldError) err).getField();
            String message = err.getDefaultMessage();
            errors.put(field, message);
        });

        ValidationErrorResponse error = new ValidationErrorResponse(LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(), "Validation Error",
            "Error en la validación de datos", errors);
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Maneja cualquier otra excepción no controlada
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Error inesperado", ex);
        ErrorResponse error = new ErrorResponse(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error", "Ocurrió un error inesperado");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Respuesta de error estándar
     */
    public static class ErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;

        public ErrorResponse(LocalDateTime timestamp, int status, String error, String message) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * Respuesta de error de validación
     */
    public static class ValidationErrorResponse {
        private final LocalDateTime timestamp;
        private final int status;
        private final String error;
        private final String message;
        private final Map<String, String> errors;

        public ValidationErrorResponse(LocalDateTime timestamp, int status, String error,
                                       String message, Map<String, String> errors) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.errors = errors;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public int getStatus() {
            return status;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, String> getErrors() {
            return errors;
        }
    }
}
