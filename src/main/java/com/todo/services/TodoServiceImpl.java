package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.response.TodoResponse;
import com.todo.exceptions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.todo.utils.Mapper.map;

@Service
public class TodoServiceImpl implements TodoService{
    @Autowired
    private TodoRepositories todoRepositories;

    @Autowired
    private Users users;


    @Override
    public List<TodoResponse> viewUndoneTask(String email) {
        Optional<User> user = users.findByEmail(email);
        if(user.isEmpty())throw new UserNotFoundException("User not found");
        List<Todo> todos = todoRepositories.findTodoByUserId(user.get().getId());
        if(todos.isEmpty()) throw new AllTaskDoneException("No unfinsihed tasks");
        List<TodoResponse> todoResponses = new ArrayList<>();
        for(Todo todo : todos){
            if(!todo.isDone()){
                todoResponses.add(map(todo));
            }
        }
        return todoResponses;
    }

    @Override
    public List<TodoResponse> viewCompletedTask(String email) {
        Optional<User> user = users.findByEmail(email);
        if(user.isEmpty())throw new UserNotFoundException("User not found");
        List<Todo> todos = todoRepositories.findTodoByUserId(user.get().getId());
        if(todos.isEmpty()) throw new AllTaskDoneException("Task not found");
        List<TodoResponse> todoResponses = new ArrayList<>();
        for(Todo todo : todos){
            if(todo.isDone()){
                todoResponses.add(map(todo));
            }
        }
        return todoResponses;
    }

//    @Override
//    public void markTaskDone(TaskRequest task) {
//        Optional<User> user = users.findByEmail(task.getUserEmail());
//        if(user.isEmpty()){
//            throw new UserNotFoundException("User not found");
//        }
//        if(isLoggedIn(user.get())){
//            throw new UserNotLoggedInException("User not logged in");
//        }
//
//        List<Todo> todos = findAllTodo(user.get().getId());
//        for(Todo todo : todos){
//            if(!todo.getTask().equals(task.getTaskToAdd()))
//                throw new TaskNotFoundException("task not found");
//
//           if(todo.getTask().equals(task.getTaskToAdd())) {
//               if(todo.isDone()){
//                   throw new TaskAlreadyMarkedException("Task already marked");
//               }
//               todo.setDone(true);
//               todoRepositories.save(todo);
//           }
//
//        }
//
//    }

    @Override
    public void markTaskDone(TaskRequest task) {
        Optional<User> user = users.findByEmail(task.getUserEmail());
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        if (isLoggedIn(user.get())) {
            throw new UserNotLoggedInException("User not logged in");
        }

        List<Todo> todos = findAllTodo(user.get().getId());
        boolean found = false;

        for (Todo todo : todos) {
            if (todo.getTask().equals(task.getTaskToAdd())) {
                found = true;
                if (todo.isDone()) {
                    throw new TaskAlreadyMarkedException("Task already marked");
                }
                todo.setDone(true);
                todoRepositories.save(todo);
                break;
            }
        }

        if (!found) {
            throw new TaskNotFoundException("task not found");
        }
    }


    @Override
    public void deleteTask(TaskRequest task) {
        Optional<User> user = users.findByEmail(task.getUserEmail());
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(isLoggedIn(user.get())){
            throw new UserNotLoggedInException("User not logged in");
        }
        Todo todo = todoRepositories.findByTask(task.getTaskToAdd().toLowerCase());
        if(todo == null){
            throw new TaskNotFoundException("Task not found");
        }
        todoRepositories.delete(todo);
    }

    @Override
    public void deleteUndoneTask(String userEmail) {
        Optional<User> user = users.findByEmail(userEmail);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(isLoggedIn(user.get())){
            throw new UserNotLoggedInException("User not logged in");
        }
        List<Todo> todos = todoRepositories.findTodoByUserId(user.get().getId());
        if(todos.isEmpty()){
            throw new TaskNotFoundException("Task not found");
        }
        for(Todo todo: todos){
            if(!todo.isDone()){
                todoRepositories.delete(todo);
            }
        }

    }

    @Override
    public void deleteFinishedTask(String userEmail) {
        Optional<User> user = users.findByEmail(userEmail);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(isLoggedIn(user.get())){
            throw new UserNotLoggedInException("User not logged in");
        }
        List<Todo> todos = todoRepositories.findTodoByUserId(user.get().getId());
        if(todos.isEmpty()){
            throw new TaskNotFoundException("Task not found");
        }
        for(Todo todo: todos){
            if(todo.isDone()){
                todoRepositories.delete(todo);
            }
        }

    }

    @Override
    public TodoResponse addTodo(TaskRequest taskRequest) {
        Optional<User> user = users.findByEmail(taskRequest.getUserEmail());
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(!user.get().isLoggedIn()){
            throw new UserNotLoggedInException("User not logged in");
        }
        List<Todo> todos = findAllTodo(user.get().getId());
        for(Todo todo: todos){
            if(todo.getTask().equals(taskRequest.getTaskToAdd()) && !todo.isDone())throw new
                    UnfinishedTaskAlreadyExistException("undone task already exist");
        }
        Todo todo = new Todo();
        todo.setUserId(user.get().getId());
        todo.setTask(taskRequest.getTaskToAdd().toLowerCase());
        return map(todoRepositories.save(todo));

    }

    @Override
    public List<Todo> findAllTodo(String id) {
        Optional<User> user = users.findUserById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(isLoggedIn(user.get())){
            throw new UserNotLoggedInException("User not logged in");
        }
        List<Todo> todos = todoRepositories.findTodoByUserId(id);
        return todos;
    }

    @Override
    public TodoResponse viewTask(String userEmail, String taskToView) {
        Optional<User> user = users.findByEmail(userEmail);
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        if(isLoggedIn(user.get())){
            throw new UserNotLoggedInException("User not logged in");
        }
        List<Todo> todos = todoRepositories.findTodoByUserId(user.get().getId());
        for(Todo todo : todos){
            if(todo.getTask().equals(taskToView)){
               return map(todo);
            }
        }
        throw new TaskNotFoundException("Task not found");
    }

    private boolean isLoggedIn(User user) {
        return !user.isLoggedIn();
    }

}
