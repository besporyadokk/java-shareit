package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto create(UserResponseDto userResponseDto);

    UserResponseDto update(Integer id, UserResponseDto userResponseDto);

    UserResponseDto getById(Integer id);

    List<UserResponseDto> getAll();

    void delete(Integer id);
}
