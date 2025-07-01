package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.security.SpringUser;
import com.example.service.PostService;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api" + "/v${app.version}" + "/user")
public class UserRestController {

    private final UserService userService;
    private final PostService postService;

    @Autowired
    public UserRestController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }


    // -------------------------------------
    // User todo может быть "обратный" интерсептор. Типо да ты получил данные.. Но не все
    // -------------------------------------
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid LoginRequest dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(dto));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @GetMapping({"/{id:[1-9]\\d*}", ""})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> getUser(@PathVariable(required = false) Long id,
                                           @AuthenticationPrincipal SpringUser springUser) {
        UserDto value = userService.getUser(id, springUser);
        return ResponseEntity.ok(value);
    }


    @PutMapping
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto,
                                              @AuthenticationPrincipal SpringUser springUser) {
        return ResponseEntity.ok(userService.updateUser(dto, springUser));
    }


    @PatchMapping("/restore")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> restoreUser(@RequestBody LoginRequest dto) {
        userService.restoreUser(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> deleteSoft(@AuthenticationPrincipal SpringUser springUser) {
        userService.deleteSoft(springUser);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------
    // Post
    // -------------------------------------
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @PostMapping("/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(@RequestBody PostDto dto, @AuthenticationPrincipal SpringUser springUser) {
        userService.checkSuchUser(springUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.createPost(dto, springUser.getId()));
    }

    @GetMapping("/{id:[1-9]\\d*}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable("id") Long userId) {
        return ResponseEntity.ok().body(postService.getPosts(userId));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @PutMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePost(@RequestBody PostDto dto, @AuthenticationPrincipal SpringUser springUser) {
        return ResponseEntity.ok(postService.updatePost(dto, springUser.getId()));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @DeleteMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePost(@PathVariable("id") Long userId,
                                        @AuthenticationPrincipal SpringUser springUser) {
        postService.deletePost(userId, springUser.getId());
        return ResponseEntity.ok().build();
    }
}
