package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LogOutRequest;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LogOutResponse;
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
        if(user.get().isLoggedIn()){
            throw new UserAllReadyLoggedInException("User is already logged in");
        }
        user.get().setLoggedIn(true);
        users.save(user.get());

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("Success");
        return loginResponse;
    }

    @Override
    public LogOutResponse logOut(LogOutRequest logOutRequest) {
        Optional<User> user = users.findByEmail(logOutRequest.getCurrentUserEmail());
        user.get().setLoggedIn(false);
        users.save(user.get());
        return new LogOutResponse();

    }
}
