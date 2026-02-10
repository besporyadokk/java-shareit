package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(Integer id, UserDto userDto);

    UserDto getById(Integer id);

    List<UserDto> getAll();

    void delete(Integer id);

    void validateUserFields(UserDto userDto);

    void checkEmailUniqueness(String email);

    boolean isValidEmail(String email);
}
