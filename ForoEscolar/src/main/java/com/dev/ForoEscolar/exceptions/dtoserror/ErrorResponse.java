package com.dev.ForoEscolar.exceptions.dtoserror;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponse(
        HttpStatus status,
        String message,
        List<String> errors) {
    public ErrorResponse(HttpStatus status, String message) {
        this(status, message, null);
    }
}
