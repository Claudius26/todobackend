package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.UserResponse;
import com.todo.dtos.request.TaskRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.SimpleAsyncTaskScheduler;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private Users users;

    @Autowired
    private TodoRepositories todoRepositories;

    @BeforeEach
    void setUp() {
        users.deleteAll();
        todoRepositories.deleteAll();
    }

    @Test
    public void registercanincreasethenumberofuser(){
        UserRequest userRequest = new UserRequest();
        userServiceImpl.register(userRequest);
        assertEquals(1, users.count());

    }

    @Test
    public void registerCanShowThatuserResponseIsNotNull(){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("email@email.com");
        userRequest.setPassword("password");
        userRequest.setLastname("lastname");
        userRequest.setFirstname("firstname");
        UserResponse userResponse = userServiceImpl.register(userRequest);
        assertNotNull(userResponse);
        assertEquals(userResponse.getEmail(), userRequest.getEmail());
    }

    @Test
    public void createTaskAndtaskRepoIsIncrreasedByOne(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        assertEquals(1, todoRepositories.count());
    }

    @Test
    public void viewTaskDisplayTheTaskAdded(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        Todo taskToView = userServiceImpl.viewTask("Go to market");
        assertNotNull(taskToView);
        assertEquals(taskRequest.getTaskToAdd(), taskToView.getTask());
        assertFalse(taskToView.isDone());
    }

    @Test
    public void viewTAskRetunTaskToViewRegardlessOfTheCase(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        Todo taskToView = userServiceImpl.viewTask("go to market");
        assertNotNull(taskToView);
        assertEquals(taskRequest.getTaskToAdd(), taskToView.getTask());
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("go to market"));
        Todo viewTask = userServiceImpl.viewTask("Go To MARKET");
        assertNotNull(viewTask);
        assertEquals(taskRequest.getTaskToAdd(), viewTask.getTask());

    }

    @Test
    public void userCanViewTaskThatAreNotDone(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        List<Todo> undoneTask = userServiceImpl.viewUndoneTask();
        assertNotNull(undoneTask);
        assertEquals(2, undoneTask.size());
    }

}