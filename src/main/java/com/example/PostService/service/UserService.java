package com.example.PostService.service;

import com.example.PostService.entity.User;
import com.example.PostService.rep.UserRepository;
import com.example.PostService.security.JwtUserDetails;
import com.example.PostService.util.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    private User findBydUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(username));
    }

    public JwtUserDetails getUserDetailsByUsername(String username) {
        var user = findBydUsername(username);
        return new JwtUserDetails(
                user.getUsername(),
                user.getPassword()
        );
    }
}
