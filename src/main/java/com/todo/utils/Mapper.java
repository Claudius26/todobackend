package com.todo.utils;

import com.todo.data.model.User;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.UserResponse;

public class Mapper {
    public static User map(UserRequest request) {
        User user = new User();
        user.setFullname(request.getFirstname()  + " " + request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        return user;
    }

    public static UserResponse map(User savedUser) {
        UserResponse userResponse = new UserResponse();
        userResponse.setFullname(savedUser.getFullname());
        userResponse.setEmail(savedUser.getEmail());
        return userResponse;
    }
}
