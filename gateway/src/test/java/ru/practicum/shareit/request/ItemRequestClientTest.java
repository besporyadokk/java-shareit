package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemRequestClient itemRequestClient;

    @BeforeEach
    void setUp() {
        itemRequestClient = new ItemRequestClient("http://localhost:9090", restTemplate);
    }

    private ItemRequestCreateDto createTestRequestDto() {
        return ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();
    }

    @Test
    @DisplayName("Создание запроса")
    void create_Success() {
        ItemRequestCreateDto requestDto = createTestRequestDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.create(requestDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение своих запросов")
    void getOwn_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getOwn(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение всех запросов")
    void getAll_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(contains("/all?from="), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getAll(1, 0, 10);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение запроса по ID")
    void getById_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemRequestClient.getById(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}