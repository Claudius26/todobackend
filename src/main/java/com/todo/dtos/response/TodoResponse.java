package com.todo.dtos.response;

import lombok.Data;

@Data
public class TodoResponse {
    private String task;
    private String isDone;
    private String dateCreated;

}
