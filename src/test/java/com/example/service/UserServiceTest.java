package com.example.service;

import com.example.dto.LoginRequest;
import com.example.dto.UserDto;
import com.example.dto.jwt.JwtToken;
import com.example.entity.StatusType;
import com.example.entity.User;
import com.example.rep.UserRepository;
import com.example.security.JwtUserDetails;
import com.example.security.SpringUser;
import com.example.util.EntityMapper;
import com.example.util.exception.NotFoundException;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityMapper mapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

   /* @Test todo
    void createUser_ShouldEncodePasswordAndSaveUser() {
        // arrange
        LocalDateTime now = LocalDateTime.now();

        var inputDto = new LoginRequest("alexandr", "1234");

        User entityToSave = new User("alexandr", "encoded_pw", "some-description");

        User savedEntity = new User(1L, 1L, StatusType.ACTIVE, now, now, null, "alexandr", "encoded_pw",
                                    "some-description", List.of());
        savedEntity.setStatus(StatusType.ACTIVE);
        savedEntity.setCreatedDate(now);
        savedEntity.setLastUpdateDate(now);

        UserDto expectedDto = new UserDto(1L, StatusType.ACTIVE, now, now, "alexandr", "some-description");

        Mockito.when(passwordEncoder.encode("1234")).thenReturn("encoded_pw");
        Mockito.when(mapper.mapToEntity(Mockito.any(UserDto.class))).thenReturn(entityToSave);
        Mockito.when(userRepository.save(entityToSave)).thenReturn(savedEntity);
        Mockito.when(mapper.mapToDto(savedEntity)).thenReturn(expectedDto);

        // act
        UserDto result = userService.createUser(inputDto);

        // assert
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals("alexandr", result.getUsername());
        Assertions.assertEquals(StatusType.ACTIVE, result.getStatus());
        Assertions.assertEquals(now, result.getCreateDate());
        Assertions.assertEquals(now, result.getLastUpdateDate());

        Mockito.verify(passwordEncoder).encode("1234");
        Mockito.verify(userRepository).save(entityToSave);
        Mockito.verify(mapper).mapToDto(savedEntity);
    }*/

    @Test
    public void getUser_shouldReturnDto() {
        LocalDateTime now = LocalDateTime.now();
        User findedUser = new User();
        UserDto expectedDto = new UserDto(1L, StatusType.ACTIVE, now, now, "alex", "desc");

        Mockito.when(userRepository.findActiveById(1L)).thenReturn(Optional.of(findedUser));
        Mockito.when(mapper.mapToDto(findedUser)).thenReturn(expectedDto);

        UserDto result = userService.getUser(1L, springUser);

        Assertions.assertEquals(expectedDto, result);
    }

    @Test
    public void getUser_shouldThrowNotFound_whenUserNotFoundById() {
        Long userId = 99L;

        Mockito.when(userRepository.findActiveById(userId)).thenReturn(Optional.empty());

        Assertions.assertThrows(NotFoundException.class, () -> userService.getUser(userId, springUser));

        Mockito.verify(userRepository).findActiveById(userId);
    }

    @Test
    public void getUser_shouldReturnAuthenticatedUser_whenUserIdIsNull() {
        var springUser = defaultSpringUser();
        var authenticatedUser = new User(1L, 1L, StatusType.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), null,
                                         "alexandr", "encoded_pw", "some-description", List.of());
        var expectedDto = new UserDto(1L, StatusType.ACTIVE, LocalDateTime.now(), LocalDateTime.now(), "alexandr",
                                      "desc");

        // Мокаем SecurityContextHolder
        var auth = Mockito.mock(Authentication.class);
        Mockito.when(auth.getPrincipal()).thenReturn(springUser);

        var securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Остальные моки
        Mockito.when(userRepository.findByUsernameAndStatus("alexandr", StatusType.ACTIVE))
                .thenReturn(Optional.of(authenticatedUser));
        Mockito.when(mapper.mapToDto(authenticatedUser)).thenReturn(expectedDto);

        var serviceUserDto = userService.getUser(null, springUser);

        Assertions.assertEquals(expectedDto, serviceUserDto);
    }


    @Test
    public void updateUser_ShouldReturnUpdatedDto() {
        var now = LocalDateTime.now();
        var esterDay = now.minusDays(1);
        var newDto = new UserDto("alexandr", "new_password", "new_description");
        var springUser = defaultSpringUser();
        var findedUser = new User(1L, 1L, StatusType.ACTIVE, esterDay, esterDay, null, "alexandr", "encoded_pw",
                                  "description", List.of());
        var mappedEntity = new User(1L, 1L, StatusType.ACTIVE, esterDay, now, null, "alexandr", "new_encode_pw",
                                    "new_description", List.of());
        var savedEntity = new User(1L, 2L, StatusType.ACTIVE, esterDay, now, null, "alexandr", "new_encode_pw",
                                   "new_description", List.of());
        var expectedDto = new UserDto(1L, StatusType.ACTIVE, esterDay, now, newDto.getUsername(), newDto.getUsername());

        Mockito.when(userRepository.findByUsernameAndStatus("alexandr", StatusType.ACTIVE))
                .thenReturn(Optional.of(findedUser));
        Mockito.when(passwordEncoder.encode("new_password")).thenReturn("new_encode_pw");
        Mockito.when(mapper.map(Mockito.any(User.class), Mockito.any(UserDto.class))).thenReturn(mappedEntity);
        Mockito.when(userRepository.save(mappedEntity)).thenReturn(savedEntity);
        Mockito.when(mapper.mapToDto(savedEntity)).thenReturn(expectedDto);

        var serviceUserDto = userService.updateUser(newDto, springUser);

        Assertions.assertEquals(serviceUserDto.getDescription(), expectedDto.getDescription());
        Mockito.verify(passwordEncoder).encode("new_password");
        Mockito.verify(userRepository).findByUsernameAndStatus("alexandr", StatusType.ACTIVE);
        Mockito.verify(userRepository).save(mappedEntity);
        Mockito.verify(mapper).mapToDto(savedEntity);
    }

    @Test
    public void restoreUser_success() {
        var loginRequest = new LoginRequest("alexandr", "1234");
        var deletedUser = new User(1L, "alexandr", "encoded_pw");
        deletedUser.setStatus(StatusType.DELETED);

        Mockito.when(userRepository.findByUsernameAndStatus("alexandr", StatusType.DELETED))
                .thenReturn(Optional.of(deletedUser));
        Mockito.when(passwordEncoder.matches("1234", "encoded_pw")).thenReturn(true);

        userService.restoreUser(loginRequest);

        Assertions.assertEquals(StatusType.ACTIVE, deletedUser.getStatus());

        Mockito.verify(passwordEncoder).matches("1234", "encoded_pw");
        Mockito.verify(userRepository).save(deletedUser);

    }


    @Test
    public void testRestoreUser_BadPassword() {
        LoginRequest loginRequest = new LoginRequest("alex", "wrong");
        User deletedUser = new User(1L, "alex", "encoded_pw");

        Mockito.when(userRepository.findByUsernameAndStatus("alex", StatusType.DELETED))
                .thenReturn(Optional.of(deletedUser));
        Mockito.when(passwordEncoder.matches("wrong", "encoded_pw")).thenReturn(false);

        Assertions.assertThrows(BadCredentialsException.class, () -> userService.restoreUser(loginRequest));
    }

    @Test
    public void testDeleteSoft_OptimisticLock() {
        var springUser = defaultSpringUser();

        Mockito.when(userRepository.existsByUsernameAndStatus("alexandr", StatusType.ACTIVE))
                .thenReturn(Optional.of(1L));
        Mockito.when(userRepository.toggleStatus("alexandr", 1L, StatusType.DELETED)).thenReturn(0);

        Assertions.assertThrows(OptimisticLockException.class, () -> userService.deleteSoft(springUser));
    }

    private SpringUser defaultSpringUser() {
        return new SpringUser(
                new JwtToken(1L, "alexandr", List.of(), Instant.now(), Instant.now().plusSeconds(100)));
    }

    @Test
    public void getUserDetailsByUsername() {
        var findedUser = new User(1L, "alexandr", "encoded_pw");

        Mockito.when(userRepository.findByUsernameAndStatus("alexandr", StatusType.ACTIVE))
                .thenReturn(Optional.of(findedUser));

        JwtUserDetails result = userService.getUserDetailsByUsername("alexandr");

        Assertions.assertEquals("alexandr", result.getUsername());
        Assertions.assertEquals("encoded_pw", result.getPassword());
        Assertions.assertEquals(1L, result.getId());
    }


    @Test
    public void testCheckSuchUser_ByIdAndStatus() {
        Mockito.when(userRepository.existsByIdAndStatus(1L, StatusType.ACTIVE)).thenReturn(Optional.of(1L));
        Assertions.assertEquals(1L, userService.checkSuchUser(1L));
    }

    @Test
    public void testCheckSuchUser_ByUsernameAndStatus() {
        Mockito.when(userRepository.existsByUsernameAndStatus("alex", StatusType.ACTIVE)).thenReturn(Optional.of(1L));
        Assertions.assertEquals(1L, userService.checkSuchUser("alex", StatusType.ACTIVE));
    }


}