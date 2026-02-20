package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemGatewayControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    private ItemRequestDto testItemDto;

    @BeforeEach
    void setUp() {
        testItemDto = ItemRequestDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("POST /items - создание вещи")
    void createItem_ValidDto_ReturnsOk() throws Exception {
        when(itemClient.create(any(ItemRequestDto.class), anyInt()))
                .thenReturn(ResponseEntity.ok().body(testItemDto));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    @DisplayName("GET /items/{id} - получение вещи для владельца")
    void getItemByIdForOwner_ValidId_ReturnsOk() throws Exception {
        when(itemClient.getByIdForOwner(eq(1), anyInt()))
                .thenReturn(ResponseEntity.ok().body(testItemDto));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /items/simple/{id} - получение вещи")
    void getItemById_ValidId_ReturnsOk() throws Exception {
        when(itemClient.getById(1))
                .thenReturn(ResponseEntity.ok().body(testItemDto));

        mockMvc.perform(get("/items/simple/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /items/{id} - обновление вещи")
    void updateItem_ValidDto_ReturnsOk() throws Exception {
        ItemRequestDto updateDto = ItemRequestDto.builder()
                .name("Updated Item")
                .build();

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1)
                .name("Updated Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemClient.update(eq(1), any(ItemRequestDto.class), anyInt()))
                .thenReturn(ResponseEntity.ok().body(responseDto));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    @DisplayName("GET /items - получение всех вещей владельца")
    void getAllByOwner_ReturnsOk() throws Exception {
        when(itemClient.getAllByOwner(1))
                .thenReturn(ResponseEntity.ok().body(new ItemRequestDto[]{testItemDto}));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /items/search - поиск вещей")
    void searchItems_ValidText_ReturnsOk() throws Exception {
        when(itemClient.search(anyString(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(new ItemRequestDto[]{}));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /items/{id} - удаление вещи")
    void deleteItem_ValidId_ReturnsOk() throws Exception {
        when(itemClient.delete(eq(1), anyInt()))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /items/{id}/comment - добавление комментария")
    void addComment_ValidDto_ReturnsOk() throws Exception {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        when(itemClient.addComment(eq(1), anyInt(), any(CommentRequestDto.class)))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk());
    }
}