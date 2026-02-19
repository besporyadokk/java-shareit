package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    private ItemRequestDto testRequestDto;

    @BeforeEach
    void setUp() {
        testRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("POST /requests - создание запроса")
    void createItemRequest_ValidDto_ReturnsCreated() throws Exception {
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();

        when(itemRequestService.createItemRequest(any(ItemRequestCreateDto.class), anyInt()))
                .thenReturn(testRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    @DisplayName("GET /requests - получение своих запросов")
    void getOwnItemRequests_ReturnsOk() throws Exception {
        when(itemRequestService.getOwnItemRequests(1))
                .thenReturn(List.of(testRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /requests/all - получение всех запросов")
    void getAllItemRequests_ValidParams_ReturnsOk() throws Exception {
        when(itemRequestService.getAllItemRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(testRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /requests/{id} - получение запроса по ID")
    void getItemRequestById_ValidId_ReturnsOk() throws Exception {
        when(itemRequestService.getItemRequestById(eq(1), anyInt()))
                .thenReturn(testRequestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }
}