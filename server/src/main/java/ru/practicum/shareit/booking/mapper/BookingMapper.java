package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingResponseStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.mapper.UserDtoMapper;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {


    public static Booking toBooking(BookingRequestDto bookingRequestDto, Item item, User booker) {
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingResponseStatus.WAITING)
                .build();
    }


    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        if (booking == null) {
            return null;
        }


        UserResponseDto bookerDto = UserDtoMapper.toUserDto(booking.getBooker());

        ItemDto itemDto = ItemDto.builder()
                .id(booking.getItem().getId())
                .name(booking.getItem().getName())
                .description(booking.getItem().getDescription())
                .available(booking.getItem().getAvailable())
                .build();

        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(bookerDto)
                .item(itemDto)
                .build();
    }


    public static BookingForItemDto toBookingForItemDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingForItemDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }
}