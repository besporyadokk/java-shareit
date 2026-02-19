package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingResponseStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingResponseDto testBookingDto;

    @BeforeEach
    void setUp() {
        testBookingDto = BookingResponseDto.builder()
                .id(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingResponseStatus.WAITING)
                .build();
    }

    @Test
    @DisplayName("POST /bookings - создание бронирования")
    void createBooking_ValidDto_ReturnsCreated() throws Exception {
        BookingRequestDto requestDto = BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingService.createBooking(any(BookingRequestDto.class), anyInt()))
                .thenReturn(testBookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("PATCH /bookings/{id} - подтверждение бронирования")
    void approveBooking_ValidParams_ReturnsOk() throws Exception {
        BookingResponseDto approvedDto = BookingResponseDto.builder()
                .id(1)
                .status(BookingResponseStatus.APPROVED)
                .build();

        when(bookingService.approveBooking(eq(1), eq(true), anyInt()))
                .thenReturn(approvedDto);

        mockMvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("GET /bookings/{id} - получение бронирования")
    void getBooking_ValidId_ReturnsOk() throws Exception {
        when(bookingService.getBookingById(eq(1), anyInt()))
                .thenReturn(testBookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /bookings - получение бронирований пользователя")
    void getUserBookings_ValidParams_ReturnsOk() throws Exception {
        when(bookingService.getUserBookings(anyInt(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(testBookingDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /bookings/owner - получение бронирований владельца")
    void getOwnerBookings_ValidParams_ReturnsOk() throws Exception {
        when(bookingService.getOwnerBookings(anyInt(), any(BookingState.class), anyInt(), anyInt()))
                .thenReturn(List.of(testBookingDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }
}