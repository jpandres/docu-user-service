package com.jpandres.docuuserservice.exception;

public class DuplicatedUserException extends RuntimeException {

    public DuplicatedUserException(String message) {
        super(message);
    }
}
