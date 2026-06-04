package com.carrental.web;

import com.carrental.exception.InvalidReservationException;
import com.carrental.exception.NoCarAvailableException;
import com.carrental.web.dto.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidReservationException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidReservation(InvalidReservationException exception) {
        return ResponseEntity.badRequest().body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(NoCarAvailableException.class)
    public ResponseEntity<ApiErrorResponse> handleNoCarAvailable(NoCarAvailableException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(exception.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableRequest() {
        return ResponseEntity.badRequest().body(new ApiErrorResponse("Invalid reservation payload"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationFailure() {
        return ResponseEntity.badRequest().body(new ApiErrorResponse("Invalid reservation payload"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException exception) {
        String message = exception.getMessage() == null ? "Invalid request payload" : exception.getMessage();
        return ResponseEntity.badRequest().body(new ApiErrorResponse(message));
    }
}
