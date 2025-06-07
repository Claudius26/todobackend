package com.todo.data.repository;

import com.todo.data.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepositories extends MongoRepository<Todo, String> {
    Todo findByTask(String task);

    List<Todo> findAllByIsDoneFalse();
}
