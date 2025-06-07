package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.TodoResponse;
import com.todo.dtos.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse register(UserRequest request);

    LoginResponse login(LoginRequest loginRequest);

    TodoResponse addTask(TaskRequest taskRequest);

    TodoResponse viewTask(String taskToView);

    List<Todo> viewUndoneTask();

    void markTaskDone(String taskToMark);

    List<Todo> viewCompletedTask();

    void deleteTask(String taskToDelete);

    void deleteUndoneTask();

    void deleteFinishedTask();

}
