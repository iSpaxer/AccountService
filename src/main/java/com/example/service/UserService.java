package com.example.service;

import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.UserRepository;
import com.example.security.JwtUserDetails;
import com.example.util.exception.NotFoundException;
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
        return repository.findByUsernameAndStatus(username, StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public JwtUserDetails getUserDetailsByUsername(String username) {
        var user = findBydUsername(username);
        return new JwtUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword()
        );
    }
}
