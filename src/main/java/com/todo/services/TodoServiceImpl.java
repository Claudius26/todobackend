package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.repository.TodoRepositories;
import com.todo.dtos.request.TaskRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService{
    @Autowired
    private TodoRepositories todoRepositories;


    @Override
    public void createTask(TaskRequest taskRequest) {
        Todo todo = new Todo();
        todo.setTask(taskRequest.getTaskToAdd().toLowerCase());
        todoRepositories.save(todo);
    }

    @Override
    public List<Todo> viewUndoneTask() {
        return todoRepositories.findAllByIsDoneFalse();
    }
}
