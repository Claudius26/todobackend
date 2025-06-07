package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.exceptions.EmailAlreadyExistException;
import com.todo.exceptions.InvalidCredentialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.todo.utils.Mapper.map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private Users users;
    @Autowired
    private TodoRepositories todoRepositories;

    @Autowired
    private TodoServiceImpl todoService;

    @Override
    public UserResponse register(UserRequest request) {
        Optional<User> user = users.findByEmail(request.getEmail());
        if(user.isPresent()){
            throw new EmailAlreadyExistException("Email already exist");
        }
        return map(users.save(map(request)));
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Optional<User> user = users.findByEmail(loginRequest.getEmail());
        if(user.isEmpty() || !user.get().getPassword().equals(loginRequest.getPassword())){
            throw new InvalidCredentialException("Invalid credentials");
        }
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Success");
        return loginResponse;
    }

    @Override
    public void addTask(TaskRequest taskRequest) {
       todoService.createTask(taskRequest);
    }

    @Override
    public Todo viewTask(String taskToView) {
        return todoRepositories.findByTask(taskToView.toLowerCase());
    }

    @Override
    public List<Todo> viewUndoneTask() {
       return todoService.viewUndoneTask();
    }

    @Override
    public void markTaskDone(String taskToMark) {
        todoService.markTaskDone(taskToMark);
    }

    @Override
    public List<Todo> viewCompletedTask() {
        return todoService.viewCompletedTask();
    }

    @Override
    public void deleteTask(String taskToDelete) {
       todoService.deleteTask(taskToDelete);
    }

    @Override
    public void deleteUndoneTask() {
       todoService.deleteUndoneTask();
    }

    @Override
    public void deleteFinishedTask() {
        todoService.deleteFinishedTask();
    }
}
