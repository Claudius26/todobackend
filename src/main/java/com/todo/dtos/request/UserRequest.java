package com.todo.dtos.request;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class UserRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String password;
}
