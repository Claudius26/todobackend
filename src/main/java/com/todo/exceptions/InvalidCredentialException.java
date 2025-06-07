package com.todo.exceptions;

public class InvalidCredentialException extends TodoApplicationException {
    public InvalidCredentialException(String message) {
        super(message);
    }
}
