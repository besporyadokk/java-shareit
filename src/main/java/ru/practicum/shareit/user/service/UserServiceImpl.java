package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        validateUserFields(userDto);
        checkEmailUniqueness(userDto.getEmail());

        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        User createdUser = userStorage.create(user);
        return UserDtoMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto update(int id, UserDto userDto) {
        User existingUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {

            if (!isValidEmail(userDto.getEmail())) {
                throw new ValidationException("Некорректный формат email: " + userDto.getEmail());
            }

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

    @Override
    public UserDto getById(int id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(int id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        userStorage.deleteById(id);
    }

    @Override
    public void validateUserFields(UserDto userDto) {
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

    @Override
    public void checkEmailUniqueness(String email) {
        userStorage.findByEmail(email).ifPresent(user -> {
            throw new ConflictException("Пользователь с email=" + email + " уже существует");
        });
    }

    @Override
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }
}