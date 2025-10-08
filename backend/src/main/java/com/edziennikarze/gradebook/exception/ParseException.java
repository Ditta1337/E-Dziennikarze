package com.edziennikarze.gradebook.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ParseException extends RuntimeException {
    public ParseException(String message) {
        super(message);
    }
}
