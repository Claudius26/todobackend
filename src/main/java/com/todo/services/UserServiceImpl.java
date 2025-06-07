package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.TodoResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.exceptions.EmailAlreadyExistException;
import com.todo.exceptions.InvalidCredentialException;
import com.todo.exceptions.UnfinishedTaskAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public TodoResponse addTask(TaskRequest taskRequest) {
        Todo found = todoRepositories.findByTask(taskRequest.getTaskToAdd());
        if(found != null && !found.isDone()){
            throw new UnfinishedTaskAlreadyExistException("Unfinshed task already exist");
        }
       return todoService.createTask(taskRequest);
    }

    @Override
    public TodoResponse viewTask(String taskToView) {

        Todo todo = todoRepositories.findByTask(taskToView.toLowerCase());
        System.out.println(todo);
        TodoResponse todoResponse = new TodoResponse();
        todoResponse.setTask(todo.getTask());
        LocalDateTime now = todo.getDateTime();
        String timeCreated = now.format(DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy HH:mm:ss"));
        todoResponse.setDateCreated(timeCreated);
        return todoResponse;
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
