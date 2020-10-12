package com.jpandres.docuuserservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<Error> details = ex.getConstraintViolations()
                .parallelStream()
                .map(CommonExceptionHandler::createMessage)
                .collect(Collectors.toList());

        var error = new ErrorResponse(BAD_REQUEST.getReasonPhrase(), details);
        log.warn("Validation constraint", ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleConversionFailedException(MethodArgumentNotValidException ex) {
        List<Error> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new Error(e.getCode() + "." + e.getField(), e.getField() + " " + e.getDefaultMessage()))
                .collect(Collectors.toList());

        var error = new ErrorResponse(BAD_REQUEST.getReasonPhrase(), fieldErrors);
        log.warn("Validation constraint", ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedUserException.class)
    @ResponseStatus(CONFLICT)
    void handleDuplictedUserException(Exception e) {
        log.error("Duplicated user", e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    void handleException(Exception e) {
        log.error("Unexpected Exception", e);
    }


    @Data
    @AllArgsConstructor
    private static class ErrorResponse {

        private String message;
        private List<Error> details;
    }

    @Data
    @AllArgsConstructor
    private static class Error {

        private String code;
        private String description;
    }

    private static Error createMessage(ConstraintViolation<?> violation) {
        final String propertyPath = violation.getPropertyPath().toString();
        if (null != propertyPath) {
            final String field = propertyPath.substring(propertyPath.indexOf(".") + 1);
            return new Error(propertyPath, field + " " + violation.getMessage());
        }
        return new Error(propertyPath, violation.getMessage());
    }
}
