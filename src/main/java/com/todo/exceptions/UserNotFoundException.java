package com.todo.exceptions;

public class UserNotFoundException extends TodoApplicationException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
