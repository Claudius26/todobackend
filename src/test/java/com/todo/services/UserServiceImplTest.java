package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LoginRequest;
import com.todo.dtos.request.UserRequest;
import com.todo.dtos.response.LoginResponse;
import com.todo.dtos.response.TodoResponse;
import com.todo.dtos.response.UserResponse;
import com.todo.dtos.request.TaskRequest;
import com.todo.exceptions.*;
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
    public void createTaskReturnTaskCreated(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        TodoResponse response = userServiceImpl.addTask(taskRequest);
        assertNotNull(response);
        assertTrue(taskRequest.getTaskToAdd().equalsIgnoreCase(response.getTask()));
    }

    @Test
    public void createtaskCannotAddUncompletedTaskTwice(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Go to market"));
        assertThrows(UnfinishedTaskAlreadyExistException.class, ()->userServiceImpl.addTask(taskRequest2));
    }

    @Test
    public void viewTaskDisplayTheTaskAdded(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TodoResponse taskToView = userServiceImpl.viewTask("Go to market");
        assertNotNull(taskToView);
        assertEquals(taskRequest.getTaskToAdd(), taskToView.getTask());
        //assertFalse(taskToView.isDone());
    }

    @Test
    public void viewTAskRetunTaskToViewRegardlessOfTheCase(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TodoResponse taskToView = userServiceImpl.viewTask("go to market");
        assertNotNull(taskToView);
        assertEquals(taskRequest.getTaskToAdd(), taskToView.getTask());
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("go to market"));
        TodoResponse viewTask = userServiceImpl.viewTask("Go To MARKET");
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
        List<TodoResponse> undoneTask = userServiceImpl.viewUndoneTask();
        assertNotNull(undoneTask);
        assertEquals(2, undoneTask.size());
    }

    @Test
    public void viewListOfUndoneTasksThrowsAllTaskDoneException(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        userServiceImpl.markTaskDone("Go to market");
        userServiceImpl.markTaskDone("Run to market");
        assertThrows(AllTaskDoneException.class, ()->userServiceImpl.viewUndoneTask());

    }

    @Test
    public void viewtaskThrowTaskNotFoundExceptionWhenTaskIsNotFound(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        assertThrows(TaskNotFoundException.class, ()->userServiceImpl.viewTask("Go To mark"));
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
    public void markCompletedTaskAsDoneThrowsExceptionWhenTaskHasAlreadyBeenmarkDone(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("go to market"));
        userServiceImpl.addTask(taskRequest);
        userServiceImpl.markTaskDone("go to market");
        Todo todo = todoRepositories.findByTask("go to market");
        assertTrue(todo.isDone());
        assertThrows(TaskAlreadyMarkedException.class, ()->{
            userServiceImpl.markTaskDone("go to market");
        });
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
    public void deleteATaskThrowsErrorWhenYouTryToDeleteTaskThatHasBeenDeleted(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        userServiceImpl.deleteTask("Go to market");
        assertThrows(TaskNotFoundException.class, ()->{
            userServiceImpl.deleteTask("Go to market");
        });
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
    public void deleteATaskThatIsNotDoneThrowsErroWhenAllTasksAreDoneOrDeleted(){

        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd(("fly to market"));
        userServiceImpl.addTask(taskRequest3);
        userServiceImpl.markTaskDone("fly to market");
        userServiceImpl.markTaskDone("run to market");
        assertThrows(TaskNotFoundException.class, ()->{
            userServiceImpl.deleteUndoneTask();
        });

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
    public void deleteTaskThatIsDoneThrowsErrorWhenAllTaskAreNotDoneOrDeleted(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd(("Go to market"));
        userServiceImpl.addTask(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd(("Run to market"));
        userServiceImpl.addTask(taskRequest2);
        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd(("fly to market"));
        userServiceImpl.addTask(taskRequest3);
        assertThrows(TaskNotFoundException.class, ()->{
            userServiceImpl.deleteFinishedTask();
        });
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