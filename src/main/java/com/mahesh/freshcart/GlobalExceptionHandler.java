package com.mahesh.freshcart;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handles @Valid errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(
                        error.getField(),
                        error.getDefaultMessage()
                )
        );

        return errors;
    }

    // Handles invalid JSON / invalid enum values
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleInvalidRequestBody(
            HttpMessageNotReadableException ex) {

        Map<String, String> errors = new HashMap<>();

        if (ex.getMessage() != null &&
                ex.getMessage().contains("OrderStatus")) {

            errors.put(
                    "status",
                    "Invalid order status. Allowed values: CREATED, CONFIRMED, CANCELLED"
            );

        } else {
            errors.put("error", "Invalid request body");
        }

        return errors;
    }
}