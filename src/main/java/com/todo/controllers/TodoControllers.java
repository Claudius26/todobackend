package com.todo.controllers;

import com.todo.dtos.request.TaskRequest;
import com.todo.dtos.response.ApiResponse;
import com.todo.dtos.response.TodoResponse;
import com.todo.exceptions.TodoApplicationException;
import com.todo.services.TodoService;
import com.todo.services.TodoServiceImpl;
import com.todo.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*", allowedHeaders = "*")
@RequestMapping("/api")
@RestController
public class TodoControllers {
    @Autowired
    private TodoServiceImpl todoService;
    @Autowired
    private UserServiceImpl userServiceImpl;

    @PostMapping("/user/addTodo")
    @ResponseBody
    public ResponseEntity<?> addTodo(@RequestBody TaskRequest taskRequest){
        try{
            TodoResponse todoResponse = todoService.addTodo(taskRequest);
            return new ResponseEntity<>(new ApiResponse(todoResponse, true), HttpStatus.CREATED);
        }catch(TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/deleteTask")
    @ResponseBody
    public ResponseEntity<?> deleteTask(@RequestBody TaskRequest taskRequest){
        try{
            todoService.deleteTask(taskRequest);
            return new ResponseEntity<>(new ApiResponse("Task deleted successfully", true), HttpStatus.OK);
        }catch (TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/viewTask")
    public ResponseEntity<?> viewTask(@RequestParam("userEmail") String userEmail, @RequestParam("taskToView") String taskToView){
        try{
            TodoResponse todoResponse = todoService.viewTask(userEmail, taskToView);
            return new ResponseEntity<>(new ApiResponse(todoResponse, true), HttpStatus.OK);
        }catch(TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/deleteUnifinishedTask")
    public ResponseEntity<?> deleteUnifinishedTask(@RequestParam("userEmail") String userEmail){
        try{
            todoService.deleteUndoneTask(userEmail);
            return new ResponseEntity<>(new ApiResponse("Task deleted successfully", true), HttpStatus.OK);
        }catch(TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/user/deleteFinishedTask")
    public ResponseEntity<?> deleteFinishedTask(@RequestParam("userEmail") String userEmail){
        try{
            todoService.deleteFinishedTask(userEmail);
            return new ResponseEntity<>(new ApiResponse("Task deleted successfully", true), HttpStatus.OK);
        }catch(TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/viewCompletedTask")
    @ResponseBody
    public ResponseEntity<?> viewCompletedTask(@RequestParam("userEmail") String userEmail){
        try {
            return new ResponseEntity<>(
                    new ApiResponse(
                            todoService.viewCompletedTask(userEmail),
                            true), HttpStatus.OK);
        }catch (TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user/viewUnfinishedTask")
    @ResponseBody
    public ResponseEntity<?> viewUnfinishedTask(@RequestParam("userEmail") String userEmail){
        try{
                return new ResponseEntity<>(
                        new ApiResponse(
                                todoService.viewUndoneTask(userEmail),
                                true), HttpStatus.OK);
            }catch (TodoApplicationException exception){
                return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
            }
    }

    @PostMapping("/user/markTask")
    @ResponseBody
    public ResponseEntity<?> markTask(@RequestBody TaskRequest taskRequest){
        try{
            todoService.markTaskDone(taskRequest);
            return new ResponseEntity<>(new ApiResponse("Task marked successfully", true), HttpStatus.OK);
        }catch (TodoApplicationException exception){
            return new ResponseEntity<>(new ApiResponse(exception.getMessage(), false), HttpStatus.BAD_REQUEST);
        }
    }
}
