package ru.practicum.shareit.booking.validation;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {

    public static void validateBookingDates(BookingRequestDto bookingRequestDto) {
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();
        LocalDateTime now = LocalDateTime.now();

        if (start == null) {
            throw new ValidationException("Дата начала бронирования не может быть null");
        }

        if (end == null) {
            throw new ValidationException("Дата окончания бронирования не может быть null");
        }

        if (start.isBefore(now)) {
            throw new ValidationException("Дата начала бронирования не может быть в прошлом");
        }

        if (end.isBefore(start)) {
            throw new ValidationException("Дата окончания бронирования не может быть раньше даты начала");
        }

        if (end.isBefore(now)) {
            throw new ValidationException("Дата окончания бронирования не может быть в прошлом");
        }

        if (start.isEqual(end)) {
            throw new ValidationException("Дата начала и окончания бронирования не могут совпадать");
        }
    }

    public static void validateBookingForCreation(BookingRequestDto bookingRequestDto,
                                                  int bookerId,
                                                  int itemOwnerId) {
        validateBookingDates(bookingRequestDto);

        if (bookerId == itemOwnerId) {
            throw new ValidationException("Владелец вещи не может бронировать свою же вещь");
        }
    }
}