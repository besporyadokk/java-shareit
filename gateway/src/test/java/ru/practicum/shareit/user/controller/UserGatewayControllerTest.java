package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserUpdateDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1);
        testUserDto.setName("Test User");
        testUserDto.setEmail("test@example.com");
    }

    @Test
    @DisplayName("POST /users - создание пользователя")
    void createUser_ValidDto_ReturnsOk() throws Exception {
        when(userClient.create(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().body(testUserDto));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /users/{id} - получение пользователя")
    void getUserById_ValidId_ReturnsOk() throws Exception {
        when(userClient.getById(1))
                .thenReturn(ResponseEntity.ok().body(testUserDto));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /users/{id} - обновление пользователя")
    void updateUser_ValidDto_ReturnsOk() throws Exception {
        UserUpdateDto updateDto = new UserUpdateDto();
        updateDto.setName("Updated Name");

        UserDto responseDto = new UserDto();
        responseDto.setId(1);
        responseDto.setName("Updated Name");
        responseDto.setEmail("test@example.com");

        when(userClient.update(eq(1), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok().body(responseDto));

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - удаление пользователя")
    void deleteUser_ValidId_ReturnsOk() throws Exception {
        when(userClient.delete(1))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /users - получение всех пользователей")
    void getAllUsers_ReturnsOk() throws Exception {
        when(userClient.getAll())
                .thenReturn(ResponseEntity.ok().body(new UserDto[]{testUserDto}));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }
}