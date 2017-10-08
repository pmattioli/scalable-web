package com.pmattioli.diffresolver.api.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.pmattioli.diffresolver.api.model.DiffApiResponse;

@ControllerAdvice
public class DiffApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = Logger.getLogger(DiffApiResponseEntityExceptionHandler.class.getName());

    @ExceptionHandler(value = { IllegalArgumentException.class, IllegalStateException.class })
    protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest request) {
        LOG.log(Level.SEVERE, "Exception caught by handler: " + ex.getMessage());
        return handleExceptionInternal(ex, new DiffApiResponse(ex.getMessage()),
                new HttpHeaders(), HttpStatus.PRECONDITION_FAILED, request);
    }
}
