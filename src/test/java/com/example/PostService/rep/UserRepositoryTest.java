package com.example.PostService.rep;

import com.example.PostService.entity.User;
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

    @Test
    public void testSoftDelete() {
        log.info("Tест testSoftDelete");
        // Создаём и сохраняем пользователя
        User user = new User();
        user.setUsername("Dmitriy");
        user.setCreatedDate(LocalDateTime.now());

        userRepository.save(user);

        // Выполняем мягкое удаление
        userRepository.delete(user);

        // Проверяем, что пользователь не виден в обычной выборке
        Optional<User> deletedUser = userRepository.findById(user.getId());
        assertFalse(deletedUser.isPresent());

        // Проверяем, что пользователь есть в удалённых
        List<User> deletedUsers = userRepository.findAllDeleted();
        assertEquals(1, deletedUsers.size());
        assertEquals("Dmitriy", deletedUsers.get(0).getUsername());
    }

    @Test
    public void testRestoreUser() {
        log.info("Tест testRestoreUser");

        // Создаём и сохраняем пользователя
        User user = new User();
        user.setUsername("Maxim");
        userRepository.save(user);
        user.setCreatedDate(LocalDateTime.now());


        // Мягкое удаление
        userRepository.delete(user);

        // Восстанавливаем
        userRepository.restoreById(user.getId());

        // Проверяем, что пользователь снова доступен
        Optional<User> restoredUser = userRepository.findById(user.getId());
        assertTrue(restoredUser.isPresent());
        assertEquals("Maxim", restoredUser.get().getUsername());
        assertTrue(restoredUser.get().isActive());
    }
}