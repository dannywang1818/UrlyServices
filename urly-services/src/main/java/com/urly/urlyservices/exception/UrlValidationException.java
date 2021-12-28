package com.urly.urlyservices.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid Input Url")
public class UrlValidationException extends RuntimeException{
    public UrlValidationException(String message) {
        super(message);
    }
}
