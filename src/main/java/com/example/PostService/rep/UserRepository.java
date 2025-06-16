package com.example.PostService.rep;

import com.example.PostService.entity.User;

import java.util.Optional;

public interface UserRepository extends AbstractRepository<User, Long> {

    Optional<User> findByUsername(String username);
}
