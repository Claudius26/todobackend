package com.todo.data.repository;

import com.todo.data.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Users extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findUserById(String id);
}
