package com.urly.urlyservices.aop;

import com.urly.urlyservices.exception.UrlValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class LongToShortControllerAdvice {

    @ExceptionHandler(value = UrlValidationException.class)
    public ResponseEntity<Object> handleUrlValidationException(UrlValidationException urlValidationException) {
        log.info("Handle UrlValidationException");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("created_at", new Date());
        body.put("message", "Invalid Input Long Url " + urlValidationException.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<Object> handleNoSuchElementException(NoSuchElementException noSuchElementException) {
        log.info("Handle NoSuchElementException");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("created_at", new Date());
        body.put("message", noSuchElementException.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}
