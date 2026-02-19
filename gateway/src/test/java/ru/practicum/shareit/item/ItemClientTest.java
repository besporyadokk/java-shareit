package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemClientTest {

    @Mock
    private RestTemplate restTemplate;

    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        itemClient = new ItemClient("http://localhost:9090", restTemplate);
    }

    private ItemRequestDto createTestItemDto() {
        return ItemRequestDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Создание вещи")
    void create_Success() {
        ItemRequestDto itemDto = createTestItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(itemDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.create(itemDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Обновление вещи")
    void update_Success() {
        ItemRequestDto itemDto = createTestItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(itemDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.update(1, itemDto, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение вещи для владельца")
    void getByIdForOwner_Success() {
        ItemRequestDto itemDto = createTestItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(itemDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getByIdForOwner(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение вещи по ID")
    void getById_Success() {
        ItemRequestDto itemDto = createTestItemDto();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(itemDto);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getById(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllByOwner_Success() {
        ItemRequestDto[] items = {createTestItemDto()};
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(items);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.getAllByOwner(1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Поиск вещей")
    void search_Success() {
        ItemRequestDto[] items = {createTestItemDto()};
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().body(items);

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class), anyMap()))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.search("drill", 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Удаление вещи")
    void delete_Success() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.delete(1, 1);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("Добавление комментария")
    void addComment_Success() {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        ResponseEntity<Object> expectedResponse = ResponseEntity.ok().build();

        when(restTemplate.exchange(anyString(), any(), any(), eq(Object.class)))
                .thenReturn(expectedResponse);

        ResponseEntity<Object> response = itemClient.addComment(1, 2, commentDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}