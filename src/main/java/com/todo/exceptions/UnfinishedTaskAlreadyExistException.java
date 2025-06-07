package com.todo.exceptions;

public class UnfinishedTaskAlreadyExistException extends TodoApplicationException{
    public UnfinishedTaskAlreadyExistException(String message) {
        super(message);
    }
}
