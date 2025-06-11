package com.todo.exceptions;

public class EmptyDetailsException extends TodoApplicationException {
    public EmptyDetailsException(String message) {
        super(message);
    }
}
