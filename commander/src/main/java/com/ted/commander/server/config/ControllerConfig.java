package com.ted.commander.server.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerConfig {

    final static Logger LOGGER = LoggerFactory.getLogger(ControllerConfig.class);

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handle(HttpMessageNotReadableException e) {
        Throwable mostSpecificCause = e.getMostSpecificCause();
        String exceptionName = mostSpecificCause.getClass().getName();
        String message = mostSpecificCause.getMessage();
        LOGGER.warn("Returning HTTP 400 Bad Request: {} {}", exceptionName, message);
//        LOGGER.warn("Returning HTTP 400 Bad Request", e);
//        throw e;
    }

}
