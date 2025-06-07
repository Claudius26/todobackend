package com.todo.data.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Todo {
    @Id
    private String id;
    private String task;
    private boolean isDone = false;
    private LocalDateTime dateTime =  LocalDateTime.now();
}
