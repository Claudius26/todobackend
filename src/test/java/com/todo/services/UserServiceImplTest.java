package com.todo.services;

import com.todo.data.model.Todo;
import com.todo.data.model.User;
import com.todo.data.repository.TodoRepositories;
import com.todo.data.repository.Users;
import com.todo.dtos.request.LogOutRequest;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplTest {
    @Autowired
    private UserServiceImpl userServiceImpl;

    @Autowired
    private Users users;

    @Autowired
    private TodoRepositories todoRepositories;
    @Autowired
    private TodoServiceImpl todoServiceImpl;

    @BeforeEach
    void setUp() {
        users.deleteAll();
        todoRepositories.deleteAll();
    }

    @Test
    public void registercanincreasethenumberofuser(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("email11@email.com");
        userRequest.setPassword("password");
        userServiceImpl.register(userRequest);
        assertEquals(1, users.count());

    }


    @Test
    public void registerCanShowThatuserResponseIsNotNull(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("email@email.com");
        userRequest.setPassword("password");

        UserResponse userResponse = userServiceImpl.register(userRequest);
        assertNotNull(userResponse);
        assertEquals(userResponse.getEmail(), userRequest.getEmail());
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
        UserResponse userResponse = userServiceImpl.register(registerUser());
        LogOutRequest logOutRequest = new LogOutRequest();
        logOutRequest.setCurrentUserEmail(userResponse.getEmail());
        userServiceImpl.logOut(logOutRequest);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("clau@gmail.com");
        loginRequest.setPassword("123456");
        LoginResponse loginResponse = userServiceImpl.login(loginRequest);
        assertNotNull(loginResponse);
        assertEquals("Success", loginResponse.getMessage());
    }

    @Test
    public void loginWillSetUserLoggedInStatusTooToBeTrueWhenAUserIsLogginIn(){
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        user.get().setLoggedIn(false);
        users.save(user.get());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("clau@gmail.com");
        loginRequest.setPassword("123456");
        userServiceImpl.login(loginRequest);
        Optional<User> updatedUser = users.findByEmail("clau@gmail.com");
        assertTrue(updatedUser.get().isLoggedIn());
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

    @Test
    public void testThatWhenTwoUsersAddsATodoEachcanFindtheirTodo(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("password");
        userServiceImpl.register(userRequest);
        UserRequest userRequest2 = new UserRequest();
        userRequest2.setFirstname("firstname");
        userRequest2.setLastname("lastname");
        userRequest2.setEmail("claud2@gmail.com");
        userRequest2.setPassword("password");
        userServiceImpl.register(userRequest2);
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd("beans");
        taskRequest.setUserEmail("claud@gmail.com");
        todoServiceImpl.addTodo(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("claud2@gmail.com");
        todoServiceImpl.addTodo(taskRequest2);
        Optional<User> user = users.findByEmail("claud@gmail.com");
        Optional<User> user2 = users.findByEmail("claud2@gmail.com");
        assertTrue(user.isPresent());
        assertTrue(user2.isPresent());


    }
    @Test
    public void taskCannotBeAddedWhenuserIsNotLoggedIn(){
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        user.get().setLoggedIn(false);
        users.save(user.get());
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd("beans");
        taskRequest.setUserEmail("clau@gmail.com");
        assertThrows(UserNotLoggedInException.class, ()->{
            todoServiceImpl.addTodo(taskRequest);
        });

    }

    @Test
    public void testThatUserCanFindNumberOfTodoheCreated(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
       taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);
        //System.out.println(response1);

        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);
        //System.out.println(response2);

        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);
        System.out.println(response3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);


        List<Todo> todo = todoServiceImpl.findAllTodo(user.get().getId());
        assertEquals(4, todoRepositories.count());
        assertEquals(2, todo.size());
    }

    @Test
    public void testThatUserCannotFindNumberOfTodoheCreatedWhenLoggedOut() {
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail(user.get().getEmail());
        todoServiceImpl.addTodo(taskRequest1);
        LogOutRequest logOutRequest = new LogOutRequest();
        logOutRequest.setCurrentUserEmail(user.get().getEmail());
        userServiceImpl.logOut(logOutRequest);
        assertThrows(UserNotLoggedInException.class, ()->{
           todoServiceImpl.findAllTodo(user.get().getId());
        });


    }

    @Test
    public void userCanDeleteTaskTheyAdded(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);
        //System.out.println(response1);

        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);
        System.out.println(response3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);

        todoServiceImpl.deleteTask(taskRequest1);


        List<Todo> todo = todoServiceImpl.findAllTodo(user.get().getId());
        assertEquals(3, todoRepositories.count());
        assertEquals(1, todo.size());
    }

    @Test
    public void testThatUserCannotDeleteTodoheCreatedWhenLoggedOut() {
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail(user.get().getEmail());
        todoServiceImpl.addTodo(taskRequest1);
        LogOutRequest logOutRequest = new LogOutRequest();
        logOutRequest.setCurrentUserEmail(user.get().getEmail());
        userServiceImpl.logOut(logOutRequest);
        assertThrows(UserNotLoggedInException.class, ()->{
            todoServiceImpl.deleteTask(taskRequest1);
        });


    }

    @Test
    public void userCanDeleteCompletedTaskTheyAdded(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);


        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);
        todoServiceImpl.markTaskDone(taskRequest1);
        todoServiceImpl.markTaskDone(taskRequest2);
        todoServiceImpl.markTaskDone(taskRequest3);
        todoServiceImpl.deleteFinishedTask(user.get().getEmail());

        List<Todo> todo = todoRepositories.findTodoByUserId(user.get().getId());
        assertEquals(2, todoRepositories.count());
        assertEquals(0, todo.size());
    }

    @Test
    public void userCannotDeleteFinishedTaskWhenLoggedOut(){
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail(user.get().getEmail());
        todoServiceImpl.addTodo(taskRequest1);
        LogOutRequest logOutRequest = new LogOutRequest();
        logOutRequest.setCurrentUserEmail(user.get().getEmail());
        userServiceImpl.logOut(logOutRequest);
        assertThrows(UserNotLoggedInException.class, ()->{
            todoServiceImpl.deleteFinishedTask(user.get().getEmail());
        });
    }

    @Test
    public void userCanDeleteUnFinishedTaskTheyAdded(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);


        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);
        todoServiceImpl.markTaskDone(taskRequest1);
        todoServiceImpl.markTaskDone(taskRequest3);
        todoServiceImpl.deleteUndoneTask(user.get().getEmail());

        List<Todo> todo = todoRepositories.findTodoByUserId(user.get().getId());
        assertEquals(3, todoRepositories.count());
        assertEquals(1, todo.size());

    }

    @Test
    public void userCanViewATask(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);


        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);

        TodoResponse todoResponse = todoServiceImpl.viewTask(taskRequest1);
        assertEquals(todoResponse.getTask(), taskRequest1.getTaskToAdd());
    }

    @Test
    public void userCanViewUnFinishedTaskTheyAdded(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);


        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);
        todoServiceImpl.markTaskDone(taskRequest1);
        todoServiceImpl.markTaskDone(taskRequest3);

        List<TodoResponse> todo = todoServiceImpl.viewUndoneTask(user.get().getEmail());
        assertEquals(4, todoRepositories.count());
        assertEquals(1, todo.size());

    }
    @Test
    public void userCanViewFinishedTaskTheyAdded(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("claud@gmail.com");
        userRequest.setPassword("123456");
        UserResponse response = userServiceImpl.register(userRequest);

        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest1 = new TaskRequest();
        taskRequest1.setTaskToAdd("buy garri");
        taskRequest1.setUserEmail("clau@gmail.com");
        TodoResponse response1 = todoServiceImpl.addTodo(taskRequest1);


        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("buy beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        TodoResponse response2 = todoServiceImpl.addTodo(taskRequest2);


        TaskRequest taskRequest3 = new TaskRequest();
        taskRequest3.setTaskToAdd("buy beans");
        taskRequest3.setUserEmail(response.getEmail());
        TodoResponse response3 = todoServiceImpl.addTodo(taskRequest3);

        TaskRequest taskRequest4 = new TaskRequest();
        taskRequest4.setTaskToAdd("buy rice");
        taskRequest4.setUserEmail(response.getEmail());
        todoServiceImpl.addTodo(taskRequest4);
        todoServiceImpl.markTaskDone(taskRequest1);
        todoServiceImpl.markTaskDone(taskRequest3);

        List<TodoResponse> todo = todoServiceImpl.viewCompletedTask(user.get().getEmail());
        assertEquals(4, todoRepositories.count());
        assertEquals(1, todo.size());

    }

    @Test
    public void userCannotLoginWhenHeIsAlreadyLoggedIn(){
        userServiceImpl.register(registerUser());
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("clau@gmail.com");
        loginRequest.setPassword("123456");
        assertThrows(UserAllReadyLoggedInException.class, () -> {
            userServiceImpl.login(loginRequest);
        });
    }

    @Test
    public void userCannotAddTaskThatHasBeenAddedButnotdone(){
        userServiceImpl.register(registerUser());
        Optional<User> user = users.findByEmail("clau@gmail.com");
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskToAdd("beans");
        taskRequest.setUserEmail("clau@gmail.com");
        todoServiceImpl.addTodo(taskRequest);
        TaskRequest taskRequest2 = new TaskRequest();
        taskRequest2.setTaskToAdd("beans");
        taskRequest2.setUserEmail("clau@gmail.com");
        assertThrows(UnfinishedTaskAlreadyExistException.class, () -> {
            todoServiceImpl.addTodo(taskRequest2);
        });
    }

    private UserRequest registerUser(){
        UserRequest userRequest = new UserRequest();
        userRequest.setFirstname("firstname");
        userRequest.setLastname("lastname");
        userRequest.setEmail("clau@gmail.com");
        userRequest.setPassword("123456");

        return userRequest;
    }

}