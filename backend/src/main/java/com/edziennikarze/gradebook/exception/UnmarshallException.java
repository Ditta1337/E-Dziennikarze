package com.edziennikarze.gradebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UnmarshallException extends RuntimeException {
    public UnmarshallException(String message) {
        super(message);
    }
}
