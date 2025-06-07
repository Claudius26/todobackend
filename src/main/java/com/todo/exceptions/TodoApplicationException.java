package com.todo.exceptions;

public class TodoApplicationException extends RuntimeException{
    public TodoApplicationException(String message){
        super(message);
    }
}
