package com.todo.utils;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.Users;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.TodoResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.exceptions.EmptyDetailsException;
import com.todo.exceptions.UserNotFoundException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Component
public class Mapper {

    public static User map(UserRequest request) {
        User user = new User();
        user.setFullname(request.getFirstname()  + " " + request.getLastname());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(request.getPassword());
        return user;
    }

    public static UserResponse map(User savedUser) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFullname(savedUser.getFullname());
        userResponse.setEmail(savedUser.getEmail());
        return userResponse;
    }

    public static TodoResponse map(Todo task){
        TodoResponse response = new TodoResponse();
        response.setTask(task.getTask());
        response.setIsDone(task.isDone() + "");
        LocalDateTime now = task.getDateTime();
        String timeCreated = now.format(DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy HH:mm:ss"));
        response.setDateCreated(timeCreated);
        return response;
    }

}
