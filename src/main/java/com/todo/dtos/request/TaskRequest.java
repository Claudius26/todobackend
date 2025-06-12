package com.todo.dtos.request;

import lombok.Data;

@Data
public class TaskRequest {
    String userEmail;
    private String taskToAdd;

    public void setTaskToAdd(String task) {
        this.taskToAdd = task != null ? task.toLowerCase() : null;
    }
}
