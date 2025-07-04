package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.PostRepository;
import com.example.rep.UserRepository;
import com.example.security.SpringUser;
import com.example.util.EntityMapper;
import com.example.util.exception.BadRequestException;
import com.example.util.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api" + "/v${app.version}" + "/user")
@Validated
public class UserRestController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityMapper mapper;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserRepository userRepository, PostRepository postRepository, EntityMapper mapper,
                              EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }


    private Long checkSuchUser(Long id, StatusType status) {
        return userRepository.existsByIdAndStatus(id, status).orElseThrow(() -> new NotFoundException(id));
    }

    private Long checkSuchUser(Long id) {
        return userRepository.existsByIdAndStatus(id, StatusType.ACTIVE).orElseThrow(() -> new NotFoundException(id));
    }

    private Long checkSuchUser(String username, StatusType status) {
        return userRepository.existsByUsernameAndStatus(username, status)
                .orElseThrow(() -> new NotFoundException(username));
    }

    // -------------------------------------
    // User
    // -------------------------------------
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        var user = userRepository.save(mapper.mapToEntity(dto));

        return ResponseEntity.created(user.getURI()).body(mapper.mapToDto(user));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @GetMapping({"/{id:[1-9]\\d*}", ""})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> get(@PathVariable(required = false) Long id) {
        User user = (id != null)
                ? userRepository.findActiveById(id).orElseThrow(() -> new NotFoundException(id))
                : getAuthenticatedUser();

        return ResponseEntity.ok(mapper.mapToDto(user));
    }

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof SpringUser springUser)) {
            throw new AuthenticationCredentialsNotFoundException("Unauthorized");
        }

        return userRepository.findByUsernameAndStatus(springUser.getUsername(), StatusType.ACTIVE)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Unauthorized. User not found."));
    }


    @PutMapping
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @ResponseStatus(HttpStatus.OK)
    // todo OptimisticLockException
    public ResponseEntity<UserDto> update(@RequestBody UserDto dto, @AuthenticationPrincipal SpringUser springUser) {
        var entity = userRepository.findByUsernameAndStatus(springUser.getUsername(), StatusType.ACTIVE)
                .orElseThrow(() -> new NotFoundException(springUser.getUsername()));
        return ResponseEntity.ok(mapper.mapToDto(userRepository.save(mapper.map(entity, dto))));
    }


    @PatchMapping("/restore")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> restore(@RequestBody LoginRequest dto) {
        var user = userRepository.findByUsernameAndStatus(dto.getUsername(), StatusType.DELETED)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Login or password not valid");
        }

        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")}
    )
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> deleteSoft(@AuthenticationPrincipal SpringUser springUser) {
        var version = checkSuchUser(springUser.getUsername(), StatusType.ACTIVE);

        if (userRepository.toggleStatus(springUser.getUsername(), version, StatusType.DELETED) == 0) {
            throw new OptimisticLockException("Optimistic lock occurred for user with id: " + springUser.getUsername());
        }
        return ResponseEntity.ok().build();
    }

    // -------------------------------------
    // Post
    // -------------------------------------
    @Operation(
            security = {@SecurityRequirement(name = "JWT")}
    )
    @PostMapping("/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(@RequestBody PostDto dto, @AuthenticationPrincipal SpringUser springUser) {
        checkSuchUser(springUser.getId());
        var post = mapper.mapToEntity(dto);

        post.setUser(entityManager.getReference(User.class, springUser.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.mapToDto(postRepository.save(post)));
    }

    @GetMapping("/{id:[1-9]\\d*}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable Long id) {
        var listPosts = postRepository.findByUserIdAndStatus(id, StatusType.ACTIVE);
        if (listPosts.isEmpty()) {
            throw new NotFoundException("Posts for User by id=" + id + " not found!");
        }
        return ResponseEntity.ok().body(mapper.mapToDto(listPosts));
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")}
    )
    @PutMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePost(@RequestBody PostDto dto, @AuthenticationPrincipal SpringUser springUser) {
        var post = postRepository.findActiveByIdAndUserId(dto.getId(), springUser.getId())
                .orElseThrow(() -> new NotFoundException(springUser.getId()));
        mapper.map(post, dto);
        return ResponseEntity.ok(mapper.mapToDto(postRepository.save(post)));
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")}
    )
    @DeleteMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePost(@PathVariable Long id, @AuthenticationPrincipal SpringUser springUser) {
        var count = postRepository.deleteByIdAndUserId(id, springUser.getId());
        if (count == 0) {
            throw new BadRequestException("Post not found!");
        }
        return ResponseEntity.ok().build();
    }
}
