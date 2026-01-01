package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto userDto) {
        return userServiceImpl.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable int id, @RequestBody UserDto userDto) {
        return userServiceImpl.update(id, userDto);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {
        return userServiceImpl.getById(id);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userServiceImpl.getAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        userServiceImpl.delete(id);
    }
}