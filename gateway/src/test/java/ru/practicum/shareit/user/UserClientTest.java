package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    private UserClient userClient;

    @BeforeEach
    void setUp() {
        userClient = new UserClient("http://localhost:9090", restTemplate);
    }

    private UserDto createTestUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        return userDto;
    }

    @Test
    @DisplayName("Создание пользователя")
    void create_Success() {
        UserDto userDto = createTestUserDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(userDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.create(userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Обновление пользователя")
    void update_Success() {
        UserDto userDto = createTestUserDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(userDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.update(1, userDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение пользователя по ID")
    void getById_Success() {
        UserDto userDto = createTestUserDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(userDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Получение всех пользователей")
    void getAll_Success() {
        UserDto[] users = {createTestUserDto()};
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(users);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.getAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void delete_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = userClient.delete(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}