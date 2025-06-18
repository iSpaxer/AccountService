package com.example.rep;

import com.example.entity.User;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
