package com.example.PostService.util.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Long id) {
        super("Entity with id=" + id + " not found!");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
