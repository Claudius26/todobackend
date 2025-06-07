package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.dtos.request.TaskRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TodoService {
    void createTask(TaskRequest taskRequest);
    List<Todo> viewUndoneTask();
    List<Todo> viewCompletedTask();
    void markTaskDone(String taskToMark);
    void deleteTask(String taskToDelete);
    void deleteUndoneTask();
    void deleteFinishedTask();
}
