package com.example.controller;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AppException.class)
    public Mono<ResponseError> handleAppException(AppException ex) {
        return Mono.just(new ResponseError(ex.getMessage()));
    }

    private record ResponseError(Error error) {
        ResponseError(String message) {
            this(new Error(message));
        }
    }

    private record Error(String message) {
    }
}
