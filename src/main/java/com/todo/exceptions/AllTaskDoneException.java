package com.todo.exceptions;

public class AllTaskDoneException extends TodoApplicationException{
    public AllTaskDoneException(String message) {
        super(message);
    }
}
