package com.todo.exceptions;

public class TaskNotFoundException extends TodoApplicationException{
    public TaskNotFoundException(String message) {
        super(message);
    }
}
