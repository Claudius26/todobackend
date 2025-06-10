package com.todo.exceptions;

public class EmptyDetailsException extends RuntimeException {
  public EmptyDetailsException(String message) {
    super(message);
  }
}
