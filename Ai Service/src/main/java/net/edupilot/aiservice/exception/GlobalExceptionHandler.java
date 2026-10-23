package net.edupilot.aiservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


/**
 * Centralized exception handler for the AI Service REST APIs.
 *
 * <p>Uses Spring's {@link RestControllerAdvice} to handle exceptions
 * thrown by controllers and return consistent error responses to clients.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles request validation failures triggered by {@code @Valid}.
     *
     * <p>Extracts the first validation error message and returns it
     * with an HTTP 400 Bad Request response.</p>
     *
     * @param exception exception containing the request validation errors
     * @return response containing the timestamp, HTTP status, and validation message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException exception) {

        // Extract the first field validation error, or use a fallback message
        // if no specific field error is available.
        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Invalid request");

        // Construct a standardized error response for the client.
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                message
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Handles exceptions raised by AI Service operations.
     *
     * <p>Returns HTTP 503 Service Unavailable to indicate that an AI-related
     * operation could not be completed.</p>
     *
     * @param exception exception describing the AI Service failure
     * @return response containing the timestamp, HTTP status, and error message
     */
    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceException(
            AiServiceException exception) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(response);
    }

    /**
     * Handles unexpected exceptions that are not covered by specific handlers.
     *
     * <p>Returns a generic error message to avoid exposing internal
     * implementation details to API clients.</p>
     *
     * @param exception unexpected exception raised during request processing
     * @return response containing the timestamp, HTTP status, and generic error message
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception exception) {

        // Keep internal exception details out of the client-facing response.
        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred"
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
