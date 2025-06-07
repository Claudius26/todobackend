package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.dtos.request.TaskRequest;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TodoService {
    public void createTask(TaskRequest taskRequest);
    public List<Todo> viewUndoneTask();
}
