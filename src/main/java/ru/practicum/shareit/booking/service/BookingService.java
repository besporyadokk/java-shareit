package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Integer bookerId);

    BookingResponseDto approveBooking(Integer bookingId, Boolean approved, Integer ownerId);

    BookingResponseDto getBookingById(Integer bookingId, Integer userId);

    List<BookingResponseDto> getUserBookings(Integer userId, BookingState state, Integer from, Integer size);

    List<BookingResponseDto> getOwnerBookings(Integer ownerId, BookingState state, Integer from, Integer size);
}