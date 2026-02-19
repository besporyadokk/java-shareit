package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserResponseDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserResponseDto();
        testUserDto.setId(1);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /users - создание пользователя")
    void createUser_ValidDto_ReturnsCreated() throws Exception {
        when(userService.create(any(UserResponseDto.class)))
                .thenReturn(testUserDto);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /users/{id} - получение пользователя")
    void getUserById_ValidId_ReturnsOk() throws Exception {
        when(userService.getById(1))
                .thenReturn(testUserDto);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"));
    }

    @Test
    @DisplayName("PATCH /users/{id} - обновление пользователя")
    void updateUser_ValidDto_ReturnsOk() throws Exception {
        UserResponseDto updateDto = new UserResponseDto();
        updateDto.setName("Updated Name");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1);
        responseDto.setName("Updated Name");
        responseDto.setEmail("test@example.com");

        when(userService.update(eq(1), any(UserResponseDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - удаление пользователя")
    void deleteUser_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /users - получение всех пользователей")
    void getAllUsers_ReturnsOk() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(testUserDto));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}