package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.response.TodoResponse;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TodoService {
    List<TodoResponse> viewUndoneTask(String email);
    List<TodoResponse> viewCompletedTask(String email);
    void markTaskDone(TaskRequest task);
    void deleteTask(TaskRequest task);
    void deleteUndoneTask(String userEmail);
    void deleteFinishedTask(String userEmail);

    TodoResponse addTodo(TaskRequest taskRequest);


    List<Todo> findAllTodo(String id);

    TodoResponse viewTask(TaskRequest taskRequest);
}
