package com.example.PostService.contoller;

import com.example.PostService.dto.PostDto;
import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.StatusType;
import com.example.PostService.entity.User;
import com.example.PostService.rep.PostRepository;
import com.example.PostService.rep.UserRepository;
import com.example.PostService.util.EntityMapper;
import com.example.PostService.util.exception.NotFoundException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@Validated
public class UserRestController {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final EntityMapper mapper;
    private final EntityManager entityManager;

    @Autowired
    public UserRestController(UserRepository userRepository, PostRepository postRepository, EntityMapper mapper, EntityManager entityManager) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.mapper = mapper;
        this.entityManager = entityManager;
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
        var user = userRepository.save(mapper.mapToEntity(dto));
        return ResponseEntity.created(user.getURI()).body(mapper.mapToDto(user));
    }


    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(mapper.mapToDto(userRepository.findActiveById(id).orElseThrow(() -> new NotFoundException(id))));
    }


    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    // todo OptimisticLockException
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        var entity = userRepository.findActiveById(id).orElseThrow(() -> new NotFoundException(id));
        return ResponseEntity.ok(mapper.mapToDto(userRepository.save(mapper.map(entity, dto))));
    }

    @PatchMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> restore(@PathVariable Long id) {
        var version = checkSuchUser(id, StatusType.DELETED);

        if (userRepository.toggleStatus(id, version, StatusType.ACTIVE) == 0) {
            throw new OptimisticLockException("Optimistic lock occurred for user with id: " + id);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
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

    @PostMapping("/{id}/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(@RequestBody PostDto dto, @PathVariable Long id) {
        checkSuchUser(id);
        var post = mapper.mapToEntity(dto);

        post.setUser(entityManager.getReference(User.class, id));
        return ResponseEntity.created(URI.create("/user/" + id + "/posts")).body(mapper.mapToDto(postRepository.save(post)));
    }

    @GetMapping("/{id}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable Long id) {
        var listPosts = postRepository.findByUserIdAndStatus(id, StatusType.ACTIVE);
        if (listPosts.isEmpty()) {
            throw new NotFoundException("Posts for User by id=" + id + " not found!");
        }
        return ResponseEntity.ok().body(mapper.mapToDto(listPosts));
    }


    @PutMapping("/{id}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePost(@RequestBody PostDto dto, @PathVariable Long id) {
        var post = postRepository.findActiveByIdAndUserId(dto.getId(), id).orElseThrow(() -> new NotFoundException(id));
        mapper.map(post, dto);
        return ResponseEntity.ok(mapper.mapToDto(postRepository.save(post)));
    }

    @DeleteMapping("/{id}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePost(@RequestBody PostDto dto, @PathVariable Long id) {
        postRepository.deleteByIdAndUserId(dto.getId(), id);
        return ResponseEntity.ok().build();
    }
}
