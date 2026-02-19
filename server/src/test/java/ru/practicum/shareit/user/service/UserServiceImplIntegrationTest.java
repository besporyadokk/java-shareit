package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.storage.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private UserResponseDto createTestUserDto() {
        UserResponseDto dto = new UserResponseDto();
        dto.setName("Test User");
        dto.setEmail("test@example.com");
        return dto;
    }

    @Test
    @DisplayName("Создание пользователя")
    void createUser_Success() {
        UserResponseDto userDto = createTestUserDto();
        UserResponseDto savedUser = userService.create(userDto);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("Test User");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Создание пользователя с уже существующим email")
    void createUser_DuplicateEmail_ThrowsConflict() {
        UserResponseDto userDto = createTestUserDto();
        userService.create(userDto);

        UserResponseDto duplicateDto = new UserResponseDto();
        duplicateDto.setName("Another User");
        duplicateDto.setEmail("test@example.com");

        assertThrows(ConflictException.class,
                () -> userService.create(duplicateDto));
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    void getUserById_Success() {
        UserResponseDto savedUser = userService.create(createTestUserDto());
        UserResponseDto retrievedUser = userService.getById(savedUser.getId());

        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(retrievedUser.getName()).isEqualTo("Test User");
        assertThat(retrievedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    void getUserById_NotFound_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> userService.getById(999));
    }

    @Test
    @DisplayName("Обновление имени пользователя")
    void updateUser_NameOnly_Success() {
        UserResponseDto savedUser = userService.create(createTestUserDto());

        UserResponseDto updateDto = new UserResponseDto();
        updateDto.setName("Updated Name");

        UserResponseDto updatedUser = userService.update(savedUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated Name");
        assertThat(updatedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Обновление email пользователя")
    void updateUser_EmailOnly_Success() {
        UserResponseDto savedUser = userService.create(createTestUserDto());

        UserResponseDto updateDto = new UserResponseDto();
        updateDto.setEmail("updated@example.com");

        UserResponseDto updatedUser = userService.update(savedUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Test User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Обновление email на уже существующий")
    void updateUser_DuplicateEmail_ThrowsConflict() {
        UserResponseDto user1 = new UserResponseDto();
        user1.setName("User 1");
        user1.setEmail("user1@example.com");
        userService.create(user1);

        UserResponseDto user2 = new UserResponseDto();
        user2.setName("User 2");
        user2.setEmail("user2@example.com");
        UserResponseDto savedUser2 = userService.create(user2);

        UserResponseDto updateDto = new UserResponseDto();
        updateDto.setEmail("user1@example.com");

        assertThrows(ConflictException.class,
                () -> userService.update(savedUser2.getId(), updateDto));
    }

    @Test
    @DisplayName("Обновление всех полей пользователя")
    void updateUser_AllFields_Success() {
        UserResponseDto savedUser = userService.create(createTestUserDto());

        UserResponseDto updateDto = new UserResponseDto();
        updateDto.setName("Updated User");
        updateDto.setEmail("updated@example.com");

        UserResponseDto updatedUser = userService.update(savedUser.getId(), updateDto);

        assertThat(updatedUser).isNotNull();
        assertThat(updatedUser.getId()).isEqualTo(savedUser.getId());
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
        assertThat(updatedUser.getEmail()).isEqualTo("updated@example.com");
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteUser_Success() {
        UserResponseDto savedUser = userService.create(createTestUserDto());
        userService.delete(savedUser.getId());

        assertThrows(NotFoundException.class,
                () -> userService.getById(savedUser.getId()));
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void deleteUser_NotFound_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> userService.delete(999));
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAllUsers_Success() {
        userService.create(createTestUserDto());

        UserResponseDto secondUser = new UserResponseDto();
        secondUser.setName("Second User");
        secondUser.setEmail("second@example.com");
        userService.create(secondUser);

        var users = userService.getAll();

        assertThat(users).hasSize(2);
    }
}