package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.UserDto;
import com.example.dto.ViewsE;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.UserRepository;
import com.example.security.DefaultAuthenticationPrincipal;
import com.example.security.JwtUserDetails;
import com.example.util.EntityMapper;
import com.example.util.exception.NotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public UserDto createUser(LoginRequest dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        var user = userRepository.save(new User((Long) null, dto.getUsername(), dto.getPassword()));
        return mapper.mapToDto(user);
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long userId, DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        var view = defaultAuthenticationPrincipal != null && (userId == null || defaultAuthenticationPrincipal.getId()
                .equals(userId))
                ? ViewsE.MYSELF
                : ViewsE.PUBLIC;
        return userRepository.findActiveByIdAndTypeView(
                        userId != null ? userId : defaultAuthenticationPrincipal.getId(), view)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    @Transactional
    public UserDto updateUser(UserDto dto, DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        var entity = userRepository.findByIdAndStatus(defaultAuthenticationPrincipal.getId(),
                                                      StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException(defaultAuthenticationPrincipal.getUsername()));
        return mapper.mapToDto(userRepository.save(mapper.map(entity, dto)));
    }

    @Transactional
    public void restoreUser(LoginRequest dto) {
        var user = userRepository.findByUsernameAndStatus(dto.getUsername(), StatusType.DELETED)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Login or password not valid");
        }

        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void deleteSoft(DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        var version = checkSuchUser(defaultAuthenticationPrincipal.getUsername(), StatusType.ACTIVE);

        if (userRepository.toggleStatus(defaultAuthenticationPrincipal.getUsername(), version,
                                        StatusType.DELETED) == 0) {
            throw new OptimisticLockException(
                    "Optimistic lock occurred for user with id: " + defaultAuthenticationPrincipal.getUsername());
        }
    }


    public JwtUserDetails getUserDetailsByUsername(String username) {
        var user = getByUsername(username);

        return new JwtUserDetails(user.getId(), user.getUsername(), user.getPassword());
    }

    @Transactional(readOnly = true)
    public Long checkSuchUser(Long id) {
        return userRepository.existsByIdAndStatus(id, StatusType.ACTIVE).orElseThrow(() -> new NotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Long checkSuchUser(String username, StatusType status) {
        return userRepository.findVersionByUsernameAndStatus(username, status)
                .orElseThrow(() -> new NotFoundException(username));
    }

    @Transactional(readOnly = true)
    private User getByUsername(String username) {
        return userRepository.findByUsernameAndStatus(username, StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

}
