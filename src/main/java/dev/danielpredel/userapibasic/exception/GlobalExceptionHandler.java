package dev.danielpredel.userapibasic.exception;

import dev.danielpredel.userapibasic.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        int status = HttpStatus.NOT_FOUND.value();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ErrorResponse response = new ErrorResponse(message, status, timestamp, null);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String message = "Invalid Requested Data";
        int status = HttpStatus.BAD_REQUEST.value();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, List<String>> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                        .add(fieldError.getDefaultMessage())
        );

        ErrorResponse response = new ErrorResponse(message, status, timestamp, errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExistsException(EmailAlreadyExistsException ex) {
        String message = "Email Already Exists";
        int status = HttpStatus.CONFLICT.value();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ErrorResponse response = new ErrorResponse(message, status, timestamp, null);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ConstraintViolationException ex) {
        String message = "Invalid Requested Data";
        int status = HttpStatus.BAD_REQUEST.value();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Map<String, List<String>> errors = new HashMap<>();

        ex.getConstraintViolations().forEach(violation -> {
            String field = StreamSupport.stream(violation.getPropertyPath().spliterator(), false)
                    .reduce((first, second) -> second)
                    .map(Object::toString)
                    .orElse("");
            errors.computeIfAbsent(field, k -> new ArrayList<>())
                    .add(violation.getMessage());
        });

        ErrorResponse response = new ErrorResponse(message, status, timestamp, errors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStockException(InsufficientStockException ex) {
        String message = ex.getMessage();
        int status = HttpStatus.CONFLICT.value();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        ErrorResponse response = new ErrorResponse(message, status, timestamp, null);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
}
