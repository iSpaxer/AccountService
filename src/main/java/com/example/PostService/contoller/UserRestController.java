package com.example.PostService.contoller;

import com.example.PostService.dto.UserDto;
import com.example.PostService.entity.StatusType;
import com.example.PostService.rep.UserRepository;
import com.example.PostService.util.EntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@Validated
public class UserRestController {

    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Autowired
    public UserRestController(UserRepository userRepository, EntityMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserDto dto) {
        userRepository.save(mapper.mapToEntity(dto));
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.mapToDto(userRepository.findActiveById(id)
                        .orElseThrow(() -> new RuntimeException("Не найден пользователь с id = " + id))
                )
        );
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    // todo OptimisticLockException
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto dto) {
        var entity = userRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id = " + id));
        return ResponseEntity.ok(
                mapper.mapToDto(userRepository.save(mapper.map(entity, dto))
            )
        );
    }

    @PatchMapping("restore/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> restore(@PathVariable Long id) {
        var user = userRepository.findDeletedById(id)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id = " + id));
        user.setStatus(StatusType.ACTIVE);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Transactional
    public ResponseEntity<?> deleteSoft(@PathVariable Long id) {
        var user = userRepository.findActiveById(id)
                .orElseThrow(() -> new RuntimeException("Не найден пользователь с id = " + id));
        user.setStatus(StatusType.DELETED);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

}
