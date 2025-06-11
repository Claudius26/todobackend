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
import com.todo.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
        if(request.getFirstname().isEmpty() ||
                request.getLastname().isEmpty() ||
                request.getEmail().isEmpty() ||
                request.getPassword().isEmpty()) {
            throw new EmptyDetailsException("Details must be filled");
        }
        Optional<User> user = users.findByEmail(request.getEmail());
        if(user.isPresent()){
            throw new EmailAlreadyExistException("Email already exist");
        }
        return map(users.save(map(request)));
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        if(loginRequest.getEmail().isEmpty() || loginRequest.getPassword().isEmpty()){
            throw new EmptyDetailsException("Details must be filled");
        }
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

        if(todo == null)throw new TaskNotFoundException("Task not found");

        return map(todo);
    }

    @Override
    public List<TodoResponse> viewUndoneTask() {

        List<TodoResponse> todoResponse = new ArrayList<>();
        List<Todo> unDoneTasks = todoService.viewUndoneTask();
        if(unDoneTasks.isEmpty()){
            throw new AllTaskDoneException("All tasks are done");
        }
       for(Todo unDone : unDoneTasks ){
           todoResponse.add(map(unDone));
       }
       return todoResponse;
    }

    @Override
    public void markTaskDone(String taskToMark) {
        todoService.markTaskDone(taskToMark.toLowerCase());
    }

    @Override
    public List<TodoResponse> viewCompletedTask() {

        List<TodoResponse> responses = new ArrayList<>();
        List<Todo> doneTasks = todoService.viewCompletedTask();
        if(doneTasks.isEmpty()){
            throw new AllTaskDoneException("All tasks are done");
        }
        for(Todo doneTask : doneTasks){
            responses.add(map(doneTask));
        }
        return responses;
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
