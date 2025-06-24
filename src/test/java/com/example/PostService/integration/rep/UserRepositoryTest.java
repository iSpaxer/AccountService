package com.example.PostService.integration.rep;

import com.example.PostService.entity.User;
import com.example.PostService.rep.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    public void testSaveAndFindUser() {
        log.info("Tест testSaveAndFindUser");
        // Создаём пользователя
        User user = new User();
        user.setCreatedDate(LocalDateTime.now());
        user.setUsername("Alexandr");

        // Сохраняем в базе
        userRepository.save(user);

        // Проверяем, что пользователь сохранён
        Optional<User> foundUser = userRepository.findById(user.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("Alexandr", foundUser.get().getUsername());
        assertTrue(foundUser.get().isActive());
    }

}
