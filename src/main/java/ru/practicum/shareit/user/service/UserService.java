package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(int id, UserDto userDto);

    UserDto getById(int id);

    List<UserDto> getAll();

    void delete(int id);

    void validateUserFields(UserDto userDto);

    void checkEmailUniqueness(String email);

    boolean isValidEmail(String email);
}
