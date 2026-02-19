package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    private ItemResponseDto testItemResponseDto;
    private ItemOwnerDto testItemOwnerDto;

    @BeforeEach
    void setUp() {
        testItemResponseDto = ItemResponseDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        testItemOwnerDto = ItemOwnerDto.builder()
                .id(1)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("POST /items - создание вещи")
    void createItem_ValidDto_ReturnsOk() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.createItem(any(ItemRequestDto.class), anyInt()))
                .thenReturn(testItemResponseDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    @DisplayName("GET /items/{id} - получение вещи для владельца")
    void getItemByIdForOwner_ValidId_ReturnsOk() throws Exception {
        when(itemService.getItemByIdForOwner(eq(1), anyInt()))
                .thenReturn(testItemOwnerDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /items/simple/{id} - получение вещи")
    void getItemById_ValidId_ReturnsOk() throws Exception {
        when(itemService.getItemById(1))
                .thenReturn(testItemResponseDto);

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

        ItemResponseDto responseDto = ItemResponseDto.builder()
                .id(1)
                .name("Updated Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.updateItem(eq(1), any(ItemRequestDto.class), anyInt()))
                .thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    @DisplayName("GET /items - получение всех вещей владельца")
    void getAllItemsByOwner_ReturnsOk() throws Exception {
        when(itemService.getAllItemsByOwner(1))
                .thenReturn(List.of(testItemOwnerDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /items/search - поиск вещей")
    void searchItems_ValidText_ReturnsOk() throws Exception {
        when(itemService.searchAvailableItems(anyString(), anyInt()))
                .thenReturn(List.of(testItemResponseDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("DELETE /items/{id} - удаление вещи")
    void deleteItem_ValidId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /items/{id}/comment - добавление комментария")
    void addComment_ValidDto_ReturnsOk() throws Exception {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        CommentDto responseDto = CommentDto.builder()
                .id(1)
                .text("Great item!")
                .authorName("User")
                .build();

        when(itemService.addComment(eq(1), anyInt(), any(CommentRequestDto.class)))
                .thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}