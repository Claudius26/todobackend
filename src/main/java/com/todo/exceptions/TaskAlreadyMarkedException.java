package com.todo.exceptions;

public class TaskAlreadyMarkedException extends TodoApplicationException{
    public TaskAlreadyMarkedException(String message) {
        super(message);
    }
}
