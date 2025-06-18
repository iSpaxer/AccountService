package com.example.controller.edvice;

import com.example.dto.ExceptionBody;
import com.example.util.exception.BusinessException;
import com.example.util.exception.NotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Collections;
import java.util.stream.Collectors;

import static com.example.util.StaticUtilsStr.POST_USER_CONSTRAINT;
import static com.example.util.StaticUtilsStr.USERNAME_CONSTRAINT;

@org.springframework.web.bind.annotation.RestControllerAdvice
@Slf4j
public class RestControllerAdvice {


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

    @ApiResponse(responseCode = "404")
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ExceptionBody> handleNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ExceptionBody("Route not found: " + ex.getResourcePath()));
    }


    @ApiResponse(responseCode = "404")
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionBody> handleResourceNotFound(NotFoundException e) {
        var body = new ExceptionBody(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(body);
    }

    @ApiResponse(responseCode = "405")
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionBody> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        var body = new ExceptionBody(ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(body);
    }

    @ApiResponse(responseCode = "409")
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleConflict(DataIntegrityViolationException ex) {
        String message;
        var dbMessage = ex.getMessage();
        if (dbMessage == null) {
            message = "Database constraint violation.";
        } else if (USERNAME_CONSTRAINT.matcher(dbMessage).find()) {
            message = "A user with this username already exists.";
        } else if (POST_USER_CONSTRAINT.matcher(dbMessage).find()) {
            message = "User for this post was not found.";
        } else {
            message = "Database constraint violation.";
            ex.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ExceptionBody(message));
    }


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        return ex.getResponseEntity();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleCommonException(Exception e) {
        log.error("Unexpected error occurred", e);
        return new ExceptionBody("INTERNAL_SERVER_ERROR", Collections.singletonMap(e.toString().substring(0, e.toString().indexOf(":")), e.getMessage()));
    }
}
