package com.example.controller;

import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.PostRepository;
import com.example.rep.UserRepository;
import com.example.util.EntityMapper;
import com.example.util.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
//@SecurityRequirement(name = "JWT")
@Validated
public class UserRestController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityMapper mapper;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(UserRepository userRepository, PostRepository postRepository, EntityMapper mapper, EntityManager entityManager, PasswordEncoder passwordEncoder) {
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

//    @PostMapping("/login")
//    @ResponseStatus(HttpStatus.OK)
//    public ResponseEntity<?> login(@RequestBody UserDto dto) {
//        var user = userRepository.findByUsername(dto.getUsername())
//                .orElseThrow(() -> new NotFoundException(dto.getUsername()));
//        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
//            throw new AccessDeniedException("Not mathes password");
//        }
//        return ResponseEntity.ok().build();
//    }


    @GetMapping("/{id:[1-9]\\d*}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.mapToDto(userRepository.findActiveById(id).orElseThrow(() -> new NotFoundException(id))));
    }


    @Operation(
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @PutMapping("/{id:[1-9]\\d*}")
    @ResponseStatus(HttpStatus.OK)
    // todo OptimisticLockException
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        var entity = userRepository.findActiveById(id).orElseThrow(() -> new NotFoundException(id));
        return ResponseEntity.ok(mapper.mapToDto(userRepository.save(mapper.map(entity, dto))));
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @PatchMapping("/{id:[1-9]\\d*}/restore")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> restore(@PathVariable Long id) {
        var version = checkSuchUser(id, StatusType.DELETED);

        if (userRepository.toggleStatus(id, version, StatusType.ACTIVE) == 0) {
            throw new OptimisticLockException("Optimistic lock occurred for user with id: " + id);
        }
        return ResponseEntity.ok().build();
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @DeleteMapping("/{id:[1-9]\\d*}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> deleteSoft(@PathVariable Long id) {
        var version = checkSuchUser(id);

        if (userRepository.toggleStatus(id, version, StatusType.DELETED) == 0) {
            throw new OptimisticLockException("Optimistic lock occurred for user with id: " + id);
        }
        return ResponseEntity.ok().build();
    }

    // -------------------------------------
    // Post
    // -------------------------------------

    @Operation(
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @PostMapping("/{id:[1-9]\\d*}/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(@RequestBody PostDto dto, @PathVariable Long id) {
        checkSuchUser(id);
        var post = mapper.mapToEntity(dto);

        post.setUser(entityManager.getReference(User.class, id));
        return ResponseEntity.created(URI.create("/user/" + id + "/posts")).body(mapper.mapToDto(postRepository.save(post)));
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
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @PutMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePost(@RequestBody PostDto dto, @PathVariable Long id) {
        var post = postRepository.findActiveByIdAndUserId(dto.getId(), id).orElseThrow(() -> new NotFoundException(id));
        mapper.map(post, dto);
        return ResponseEntity.ok(mapper.mapToDto(postRepository.save(post)));
    }

    @Operation(
            security = {@SecurityRequirement(name = "JWT")} // <-- вот это добавляет замочек
    )
    @DeleteMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePost(@RequestBody PostDto dto, @PathVariable Long id) {
        postRepository.deleteByIdAndUserId(dto.getId(), id);
        return ResponseEntity.ok().build();
    }
}
