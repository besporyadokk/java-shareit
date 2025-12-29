package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public UserDto create(UserDto userDto) {
        // Валидация полей вручную
        validateUserFields(userDto);

        // Проверка на уникальность email
        checkEmailUniqueness(userDto.getEmail());

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        User createdUser = userStorage.create(user);
        return UserDtoMapper.toUserDto(createdUser);
    }

    public UserDto update(int id, UserDto userDto) {
        User existingUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            // Проверка email на валидность
            if (!isValidEmail(userDto.getEmail())) {
                throw new ValidationException("Некорректный формат email: " + userDto.getEmail());
            }

            // Проверка на уникальность email при обновлении (кроме текущего пользователя)
            if (!userDto.getEmail().equals(existingUser.getEmail())) {
                checkEmailUniqueness(userDto.getEmail());
            }
            existingUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userStorage.update(existingUser);
        if (updatedUser == null) {
            throw new NotFoundException("Ошибка при обновлении пользователя с id=" + id);
        }
        return UserDtoMapper.toUserDto(updatedUser);
    }

    public UserDto getById(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return UserDtoMapper.toUserDto(user);
    }

    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    public void delete(int id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        userStorage.deleteById(id);
    }

    private void validateUserFields(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new ValidationException("Имя пользователя не может быть пустым");
        }

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new ValidationException("Email не может быть пустым");
        }

        if (!isValidEmail(userDto.getEmail())) {
            throw new ValidationException("Некорректный формат email: " + userDto.getEmail());
        }
    }

    private void checkEmailUniqueness(String email) {
        boolean emailExists = userStorage.findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));

        if (emailExists) {
            throw new ConflictException("Пользователь с email=" + email + " уже существует");
        }
    }

    private boolean isValidEmail(String email) {
        // Простая проверка email
        return email != null && email.contains("@") && email.contains(".");
    }
}