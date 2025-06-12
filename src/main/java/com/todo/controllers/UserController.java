package com.todo.controllers;

import com.todo.dtos.request.LogOutRequest;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.ApiResponse;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.TodoResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.exceptions.TodoApplicationException;
import com.todo.services.TodoService;
import com.todo.services.UserService;
import com.todo.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*", allowedHeaders = "*")
@RequestMapping("/api")
@RestController
public class UserController {
    @Autowired
    private UserServiceImpl userServiceImpl;
    @Autowired
    private TodoService todoService;
    @Autowired
    private UserService userService;

    @PostMapping(value = "/user/register", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> register(@RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = userServiceImpl.register(userRequest);
            return new ResponseEntity<>(new ApiResponse(userResponse, true), HttpStatus.CREATED);
        }catch(TodoApplicationException exception){
            return new  ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/user/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        try {
            LoginResponse response = userServiceImpl.login(loginRequest);
            return new ResponseEntity<>(new ApiResponse(response, true), HttpStatus.OK);
        } catch (TodoApplicationException exception) {
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }

    }

    @PostMapping("/user/logOut")
    public ResponseEntity<?> logout(@RequestBody LogOutRequest logOutRequest) {
        try{
            userServiceImpl.logOut(logOutRequest);
            return new ResponseEntity<>(new ApiResponse(true, true), HttpStatus.OK);
        }catch(TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

}
