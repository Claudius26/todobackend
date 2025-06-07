package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.UserResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Users users;
    @Autowired
    private TodoRepositories todoRepositories;

    @Override
    public UserResponse register(UserRequest request) {
        User user = new User();
        user.setFullname(request.getFirstname()  + " " + request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User savedUser = users.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setFullname(savedUser.getFullname());
        userResponse.setEmail(savedUser.getEmail());
        return userResponse;
    }

    @Override
    public void addTask(TaskRequest taskRequest) {
        Todo todo = new Todo();
        todo.setTask(taskRequest.getTaskToAdd().toLowerCase());
        todoRepositories.save(todo);
    }

    @Override
    public Todo viewTask(String taskToView) {
        return todoRepositories.findByTask(taskToView.toLowerCase());
    }
}
