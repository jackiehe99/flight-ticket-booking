package com.example.flightbooking.api;

import com.example.flightbooking.service.BookingNotFoundException;
import com.example.flightbooking.service.FlightFullException;
import com.example.flightbooking.service.FlightNotFoundException;
import java.util.stream.Collectors;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(FlightNotFoundException.class)
  public ResponseEntity<ApiError> flightNotFound(FlightNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of("FLIGHT_NOT_FOUND", ex.getMessage()));
  }

  @ExceptionHandler(BookingNotFoundException.class)
  public ResponseEntity<ApiError> bookingNotFound(BookingNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of("BOOKING_NOT_FOUND", ex.getMessage()));
  }

  @ExceptionHandler(FlightFullException.class)
  public ResponseEntity<ApiError> flightFull(FlightFullException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of("FLIGHT_FULL", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> argumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
    String msg = ex.getName() + " is invalid";
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("VALIDATION_ERROR", msg));
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    String msg =
        ex.getBindingResult().getFieldErrors().stream()
            .map(RestExceptionHandler::fieldErrorToMessage)
            .collect(Collectors.joining("; "));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("VALIDATION_ERROR", msg));
  }

  private static String fieldErrorToMessage(FieldError fe) {
    if (fe.getDefaultMessage() == null) {
      return fe.getField() + " is invalid";
    }
    return fe.getField() + " " + fe.getDefaultMessage();
  }
}

