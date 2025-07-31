package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.PostDto;
import com.example.dto.UserDto;
import com.example.entity.StatusType;
import com.example.security.DefaultAuthenticationPrincipal;
import com.example.service.PostService;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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


    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid LoginRequest dto) {
        UserDto user = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @GetMapping({"/{id:[1-9]\\d*}", ""})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> getUser(@PathVariable(required = false) Long id,
                                           @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        UserDto value = userService.getUser(id, defaultAuthenticationPrincipal);
        return ResponseEntity.ok(value);
    }


    @PutMapping
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto dto,
                                              @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        return ResponseEntity.ok(userService.updateUser(dto, defaultAuthenticationPrincipal));
    }


    @PatchMapping("/restore")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> restoreUser(@RequestBody LoginRequest dto) {
        userService.restoreUser(dto);
        return ResponseEntity.ok().build();
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deleteSoft(
            @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        userService.deleteSoft(defaultAuthenticationPrincipal);
        return ResponseEntity.ok().build();
    }

    // -------------------------------------
    // Post
    // -------------------------------------
    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @PostMapping("/post/create")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createPost(@RequestBody PostDto dto,
                                        @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        userService.checkSuchUser(defaultAuthenticationPrincipal.getId(), StatusType.ACTIVE);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(postService.createPost(dto, defaultAuthenticationPrincipal.getId()));
    }

    @GetMapping("/{id:[1-9]\\d*}/posts")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<PostDto>> getPosts(@PathVariable("id") Long userId) {
        return ResponseEntity.ok().body(postService.getPosts(userId));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @PutMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> updatePost(@RequestBody PostDto dto,
                                        @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        return ResponseEntity.ok(postService.updatePost(dto, defaultAuthenticationPrincipal.getId()));
    }

    @Operation(security = {@SecurityRequirement(name = "JWT")})
    @DeleteMapping("/{id:[1-9]\\d*}/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deletePost(@PathVariable("id") Long userId,
                                        @AuthenticationPrincipal DefaultAuthenticationPrincipal defaultAuthenticationPrincipal) {
        postService.deletePost(userId, defaultAuthenticationPrincipal.getId());
        return ResponseEntity.ok().build();
    }
}
