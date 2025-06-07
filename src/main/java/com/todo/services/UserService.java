package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {
    UserResponse register(UserRequest request);

    void addTask(TaskRequest taskRequest);

    Todo viewTask(String taskToView);

    List<Todo> viewUndoneTask();
}
