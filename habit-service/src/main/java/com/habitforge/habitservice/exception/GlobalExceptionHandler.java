package com.habitforge.habitservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HabitNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(
        HabitNotFoundException exception) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            Map.of(
                "status", 404,
                "error", "Not Found",
                "message", exception.getMessage()
            )
        );
    }

    @ExceptionHandler(InvalidHabitRequestException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidHabit(
        InvalidHabitRequestException exception) {

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Bad Request",
                "message", exception.getMessage()
            )
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
        MethodArgumentNotValidException exception) {

        Map<String, String> fieldErrors = new TreeMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
            fieldErrors.putIfAbsent(
                error.getField(),
                error.getDefaultMessage()
            )
        );

        return ResponseEntity.badRequest().body(
            Map.of(
                "status", 400,
                "error", "Validation Failed",
                "fieldErrors", fieldErrors
            )
        );
    }
}