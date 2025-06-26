package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.UserDto;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.UserRepository;
import com.example.security.JwtUserDetails;
import com.example.security.SpringUser;
import com.example.util.EntityMapper;
import com.example.util.exception.NotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final EntityMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, EntityMapper mapper, PasswordEncoder passwordEncoder) {
        this.userRepository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDto createUser(LoginRequest dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        var user = userRepository.save(new User((Long) null, dto.getUsername(), dto.getPassword()));
        return mapper.mapToDto(user);
    }

    public UserDto getUser(Long userId) {
        User user = (userId != null) ? userRepository.findActiveById(userId)
                .orElseThrow(() -> new NotFoundException(userId)) : getAuthenticatedUser();

        return mapper.mapToDto(user);
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SpringUser springUser)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }

        return userRepository.findByUsernameAndStatus(springUser.getUsername(), StatusType.ACTIVE)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Unauthorized. User not found."));
    }

    public UserDto updateUser(UserDto dto, SpringUser springUser) {
        if (!dto.getPassword().isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        var entity = userRepository.findByUsernameAndStatus(springUser.getUsername(), StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException(springUser.getUsername()));
        return mapper.mapToDto(userRepository.save(mapper.map(entity, dto)));
    }

    public void restoreUser(LoginRequest dto) {
        var user = userRepository.findByUsernameAndStatus(dto.getUsername(), StatusType.DELETED)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Login or password not valid");
        }

        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);
    }

    public void deleteSoft(SpringUser springUser) {
        var version = checkSuchUser(springUser.getUsername(), StatusType.ACTIVE);

        if (userRepository.toggleStatus(springUser.getUsername(), version, StatusType.DELETED) == 0) {
            throw new OptimisticLockException("Optimistic lock occurred for user with id: " + springUser.getUsername());
        }
    }


    public JwtUserDetails getUserDetailsByUsername(String username) {
        var user = getByUsername(username);

        return new JwtUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }

    public Long checkSuchUser(Long id) {
        return userRepository.existsByIdAndStatus(id, StatusType.ACTIVE).orElseThrow(() -> new NotFoundException(id));
    }

    public Long checkSuchUser(String username, StatusType status) {
        return userRepository.existsByUsernameAndStatus(username, status)
                .orElseThrow(() -> new NotFoundException(username));
    }

    private User getByUsername(String username) {
        return userRepository.findByUsernameAndStatus(username, StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }
}
