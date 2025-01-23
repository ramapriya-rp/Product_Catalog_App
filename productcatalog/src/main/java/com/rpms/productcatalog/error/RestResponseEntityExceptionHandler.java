package com.rpms.productcatalog.error;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.rpms.productcatalog.model.ErrorMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ControllerAdvice
@ResponseStatus
public class RestResponseEntityExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFoundException(ProductNotFoundException exception, WebRequest request) {
        LOGGER.info("Handling ProductNotFoundException. Root cause: {}",exception.getLocalizedMessage());
        ErrorMessage message = new ErrorMessage(HttpStatus.NOT_FOUND, exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        LOGGER.info("Handling MethodArgumentNotValidException. Reason: Issues in input sent in the request");
        List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorMessage> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        LOGGER.info("Handling IllegalArgumentException. Reason: {}", exception.getMessage());
        ErrorMessage message = new ErrorMessage(HttpStatus.BAD_REQUEST, exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        LOGGER.info("Handling HttpMessageNotReadableException. Reason: {}", ex.getMessage());
        StringBuilder errorMessage = new StringBuilder("Invalid request body: Unable to read the message. ");
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException invalidFormatException) {
            errorMessage.append("\nProblematic field: ");
            for (JsonMappingException.Reference reference : invalidFormatException.getPath()) {
                errorMessage.append(reference.getFieldName()).append(" ");
            }
            errorMessage.append("\n");
        }
        errorMessage.append("Please check your input.");
        return new ResponseEntity<>(errorMessage.toString(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> errors = new HashMap<>();
        LOGGER.info("Handling Generic exceptions. Reason: {}", ex.getMessage());
        errors.put("GENERIC EXCEPTION Data Errors ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }


}
