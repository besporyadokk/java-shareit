package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userStorage;

    public UserResponseDto create(UserResponseDto userResponseDto) {
        if (userStorage.findByEmail(userResponseDto.getEmail()).isPresent()) {
            throw new ConflictException("Пользователь с email=" + userResponseDto.getEmail() + " уже существует");
        }

        User user = new User();
        user.setName(userResponseDto.getName());
        user.setEmail(userResponseDto.getEmail());
        User createdUser = userStorage.save(user);
        return UserDtoMapper.toUserDto(createdUser);
    }

    @Override
    public UserResponseDto update(Integer id, UserResponseDto userResponseDto) {
        User existingUser = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));

        if (userResponseDto.getEmail() != null &&
                !userResponseDto.getEmail().equals(existingUser.getEmail())) {
            if (userStorage.findByEmail(userResponseDto.getEmail()).isPresent()) {
                throw new ConflictException("Пользователь с email=" + userResponseDto.getEmail() + " уже существует");
            }
            existingUser.setEmail(userResponseDto.getEmail());
        }

        if (userResponseDto.getName() != null) {
            existingUser.setName(userResponseDto.getName());
        }

        User updatedUser = userStorage.save(existingUser);
        return UserDtoMapper.toUserDto(updatedUser);
    }

    @Override
    public UserResponseDto getById(Integer id) {
        User user = userStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + id + " не найден"));
        return UserDtoMapper.toUserDto(user);
    }

    @Override
    public List<UserResponseDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserDtoMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Integer id) {
        if (!userStorage.existsById(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        userStorage.deleteById(id);
    }

}