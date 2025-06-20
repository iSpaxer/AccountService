package com.example.util.exception;

import com.example.dto.ExceptionBody;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

@Getter
public class BusinessException extends RuntimeException {
    private final HttpStatus httpStatus;

    public BusinessException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public BusinessException(HttpStatus httpStatus, Supplier<String> message) {
        super(message.get());
        this.httpStatus = httpStatus;
    }

    public ResponseEntity<?> getResponseEntity() {
        if (this.getMessage() != null && this.getMessage().isBlank()) {
            return ResponseEntity
                    .status(httpStatus)
                    .body(new ExceptionBody(this.getMessage()));
        } else {
            return ResponseEntity
                    .status(httpStatus)
                    .build();
        }

    }
}
