package com.jpandres.docuuserservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<ErrorResponse> handleConversionFailedException(MethodArgumentNotValidException ex) {
        List<Error> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new Error(e.getCode() + "." + e.getField(), e.getField() + " " + e.getDefaultMessage()))
                .sorted(Comparator.comparing(t -> t.description))
                .collect(Collectors.toList());

        var error = new ErrorResponse(BAD_REQUEST.getReasonPhrase(), fieldErrors);
        log.warn("Validation constraint", ex);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicatedUserException.class)
    @ResponseStatus(CONFLICT)
    void handleDuplicatedUserException(Exception e) {
        log.error("Duplicated user", e);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    void handleException(Exception e) {
        log.error("Unexpected Exception", e);
    }

    @Getter
    @AllArgsConstructor
    private static class ErrorResponse {

        private final String message;
        private final List<Error> details;
    }

    @Getter
    @AllArgsConstructor
    private static class Error {

        private final String code;
        private final String description;
    }
}
