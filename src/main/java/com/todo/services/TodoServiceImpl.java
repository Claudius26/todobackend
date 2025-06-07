package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.repository.TodoRepositories;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.response.TodoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.todo.utils.Mapper.map;

@Service
public class TodoServiceImpl implements TodoService{
    @Autowired
    private TodoRepositories todoRepositories;


    @Override
    public TodoResponse createTask(TaskRequest taskRequest) {
        return map(todoRepositories.save(map(taskRequest)));
    }

    @Override
    public List<Todo> viewUndoneTask() {
        return todoRepositories.findAllByIsDoneFalse();
    }

    @Override
    public List<Todo> viewCompletedTask() {
        return todoRepositories.findAllByIsDoneTrue();
    }

    @Override
    public void markTaskDone(String taskToMark) {
        Todo todo = todoRepositories.findByTask(taskToMark.toLowerCase());
        todo.setDone(true);
        todoRepositories.save(todo);
    }

    @Override
    public void deleteTask(String taskToDelete) {
        Todo todo = todoRepositories.findByTask(taskToDelete.toLowerCase());
        todoRepositories.delete(todo);
    }

    @Override
    public void deleteUndoneTask() {
        List<Todo> unFinishedTask = viewUndoneTask();
        todoRepositories.deleteAll(unFinishedTask);
    }

    @Override
    public void deleteFinishedTask() {
        List<Todo> completedTask = viewCompletedTask();
        todoRepositories.deleteAll(completedTask);
    }

}
