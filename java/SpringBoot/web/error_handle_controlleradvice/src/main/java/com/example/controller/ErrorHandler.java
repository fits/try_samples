package com.example.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(AppException.class)
    public ResponseError handleAppException(AppException ex) {
        return new ResponseError(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseError handleBadRequest() {
        return new ResponseError("Bad Request");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseError handleNotFound() {
        return new ResponseError("Not Found");
    }

    private record ResponseError(Error error) {
        ResponseError(String message) {
            this(new Error(message));
        }
    }

    private record Error(String message) {
    }
}
