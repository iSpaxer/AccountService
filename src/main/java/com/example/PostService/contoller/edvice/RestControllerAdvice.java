package com.example.PostService.contoller.edvice;

import com.example.PostService.dto.ExceptionBody;
import com.example.PostService.util.exception.NotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.stream.Collectors;

@org.springframework.web.bind.annotation.RestControllerAdvice
@Slf4j
public class RestControllerAdvice {

    @ApiResponse(responseCode = "404")
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionBody> handleResourceNotFound(NotFoundException e) {
        var body = new ExceptionBody(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ApiResponse(responseCode = "400")
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ExceptionBody> handleOptimisticLock(OptimisticLockException e) {
        var body = new ExceptionBody(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ApiResponse(responseCode = "400")
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionBody> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        var body = new ExceptionBody("Invalid JSON format");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ApiResponse(responseCode = "400")
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionBody> handleConstraintViolation(ConstraintViolationException e) {
        var errors = e.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (existing, replacement) -> existing
                ));
        var body = new ExceptionBody("Validation failed", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(body);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleCommonException(Exception e) {
        log.error("Unexpected error occurred", e);
        return new ExceptionBody("INTERNAL_SERVER_ERROR", Collections.singletonMap(e.toString().substring(0, e.toString().indexOf(":")), e.getMessage()));
    }
}
