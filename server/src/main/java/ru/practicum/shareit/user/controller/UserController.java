package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto create(@RequestBody UserResponseDto userResponseDto) {
        return userServiceImpl.create(userResponseDto);
    }

    @PatchMapping("/{id}")
    public UserResponseDto update(@PathVariable int id, @RequestBody UserResponseDto userResponseDto) {
        return userServiceImpl.update(id, userResponseDto);
    }

    @GetMapping("/{id}")
    public UserResponseDto getById(@PathVariable Integer id) {
        return userServiceImpl.getById(id);
    }

    @GetMapping
    public List<UserResponseDto> getAll() {
        return userServiceImpl.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        userServiceImpl.delete(id);
    }
}