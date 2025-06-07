package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.dtos.request.TaskRequest;
import com.todo.exceptions.EmailAlreadyExistException;
import com.todo.exceptions.InvalidCredentialException;
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
        userRequest.setEmail("email11@email.com");
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

    @Test
    public void markCompletedTaskAsDone(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("go to market"));
        userServiceImpl.addTask(taskRequest);
         userServiceImpl.markTaskDone("go to market");
         Todo todo = todoRepositories.findByTask("go to market");
         assertTrue(todo.isDone());

    }

    @Test
    public void viewCompletedTasksReturnTaskThatHasBeenCompletedTest(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd(("fly to market"));
        userServiceImpl.addTask(taskRequest3);
        userServiceImpl.markTaskDone("fly to market");
        userServiceImpl.markTaskDone("go to market");
        List<Todo> completedTask = userServiceImpl.viewCompletedTask();
        assertNotNull(completedTask);
        assertEquals(2, completedTask.size());
    }

    @Test
    public void userCanDelelteATask(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        userServiceImpl.deleteTask("Go to market");
        assertEquals(todoRepositories.count(), 0);
    }

    @Test
    public void userCanDeleteATaskThatIsNotDone(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);

        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);

        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd(("fly to market"));
        userServiceImpl.addTask(taskRequest3);

        userServiceImpl.markTaskDone("fly to market");
        userServiceImpl.markTaskDone("go to market");
        userServiceImpl.deleteUndoneTask();
        assertEquals(2, todoRepositories.count());
    }

    @Test
    public void userCanDeleteATaskThatIsDone(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd(("fly to market"));
        userServiceImpl.addTask(taskRequest3);
        userServiceImpl.markTaskDone("fly to market");
        userServiceImpl.markTaskDone("go to market");
        userServiceImpl.deleteFinishedTask();
        assertEquals(1, todoRepositories.count());
    }

    @Test
    public void twoUserscannotRegisterWithSameEmail(){
        userServiceImpl.register(registerUser());
        assertEquals(1, users.count());
        assertThrows(EmailAlreadyExistException.class, ()->{
            userServiceImpl.register(registerUser());
        });
    }

    @Test
    public void userCanLogin(){
        userServiceImpl.register(registerUser());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("clau@gmail.com");
        loginRequest.setPassword("1234");
        LoginResponse loginResponse = userServiceImpl.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals("Success", loginResponse.getMessage());
    }

    @Test
    public void loginThrowserrorWhenEmaildontmatch(){
        userServiceImpl.register(registerUser());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cla@gmail.com");
        loginRequest.setPassword("1234");
        assertThrows(InvalidCredentialException.class, ()->{
            userServiceImpl.login(loginRequest);
        });
    }

    @Test
    public void loginThrowserrorWhenPassworddontmatch(){
        userServiceImpl.register(registerUser());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("clau@gmail.com");
        loginRequest.setPassword("1224");
        assertThrows(InvalidCredentialException.class, ()->{
            userServiceImpl.login(loginRequest);
        });
    }
    @Test
    public void loginThrowserrorWhenEmailAndPassworddontmatch(){
        userServiceImpl.register(registerUser());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("cla@gmail.com");
        loginRequest.setPassword("1224");
        assertThrows(InvalidCredentialException.class, ()->{
            userServiceImpl.login(loginRequest);
        });
    }

    private UserRequest registerUser(){
        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("clau@gmail.com");
        userRequest.setPassword("1234");
        userRequest.setLastname("lastname");
        userRequest.setFirstname("firstname");
        return userRequest;
    }

}