package com.todo.exceptions;

public class EmailAlreadyExistException extends TodoApplicationException{
    public EmailAlreadyExistException(String message) {
        super("Email already exists!!");
    }
}
