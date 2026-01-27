package ru.practicum.shareit.item.mapper;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ItemDtoMapper {

    public static ItemRequestDto toRequestDto(Item model) {
        return ItemRequestDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .owner(model.getOwner())
                .build();
    }

    public static ItemResponseDto toResponseDto(Item model, CommentRepository commentRepository) {
        List<CommentDto> comments = getCommentsForItem(model.getId(), commentRepository);

        return ItemResponseDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .comments(comments)
                .build();
    }

    public static ItemOwnerDto toOwnerDto(Item item, BookingRepository bookingRepository,
                                          CommentRepository commentRepository) {
        LocalDateTime now = LocalDateTime.now();

        // Используем методы findLastBookingForItem и findNextBookingForItem
        BookingForItemDto lastBooking = bookingRepository
                .findLastBookingForItem(
                        item.getId(),
                        BookingStatus.APPROVED,
                        now,
                        PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(BookingMapper::toBookingForItemDto)
                .orElse(null);

        BookingForItemDto nextBooking = bookingRepository
                .findNextBookingForItem(
                        item.getId(),
                        BookingStatus.APPROVED,
                        now,
                        PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(BookingMapper::toBookingForItemDto)
                .orElse(null);

        // Получаем комментарии
        List<CommentDto> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    private static BookingForItemDto getLastBooking(Integer itemId, BookingRepository bookingRepository) {
        if (bookingRepository == null) return null;

        Pageable pageable = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findLastBookingForItem(
                itemId, BookingStatus.APPROVED, now, pageable);

        if (!bookings.isEmpty()) {
            return BookingMapper.toBookingForItemDto(bookings.get(0));
        }
        return null;
    }

    private static BookingForItemDto getNextBooking(Integer itemId, BookingRepository bookingRepository) {
        if (bookingRepository == null) return null;

        Pageable pageable = PageRequest.of(0, 1);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = bookingRepository.findNextBookingForItem(
                itemId, BookingStatus.APPROVED, now, pageable);

        if (!bookings.isEmpty()) {
            return BookingMapper.toBookingForItemDto(bookings.get(0));
        }
        return null;
    }

    private static List<CommentDto> getCommentsForItem(Integer itemId, CommentRepository commentRepository) {
        if (commentRepository == null) return Collections.emptyList();

        return commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Item toItem(ItemRequestDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(dto.getOwner())
                .build();
    }

    public static Item updateItem(Item existingItem, ItemRequestDto updateDto) {
        Item.ItemBuilder builder = Item.builder()
                .id(existingItem.getId())
                .owner(existingItem.getOwner());

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            builder.name(updateDto.getName());
        } else {
            builder.name(existingItem.getName());
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            builder.description(updateDto.getDescription());
        } else {
            builder.description(existingItem.getDescription());
        }

        if (updateDto.getAvailable() != null) {
            builder.available(updateDto.getAvailable());
        } else {
            builder.available(existingItem.getAvailable());
        }

        return builder.build();
    }
}