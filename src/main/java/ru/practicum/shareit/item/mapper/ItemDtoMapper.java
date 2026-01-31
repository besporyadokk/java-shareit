package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public class ItemDtoMapper {

    public static ItemRequestDto toRequestDto(Item model) {
        if (model == null) {
            return null;
        }

        return ItemRequestDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .owner(model.getOwner())
                .build();
    }

    public static ItemResponseDto toResponseDto(Item model, List<CommentDto> comments) {
        if (model == null) {
            return null;
        }

        return ItemResponseDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .comments(comments != null ? comments : java.util.Collections.emptyList())
                .build();
    }

    public static ItemOwnerDto toOwnerDto(Item item,
                                          BookingForItemDto lastBooking,
                                          BookingForItemDto nextBooking,
                                          List<CommentDto> comments) {
        if (item == null) {
            return null;
        }

        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments != null ? comments : java.util.Collections.emptyList())
                .build();
    }

    public static Item toItem(ItemRequestDto dto) {
        if (dto == null) {
            return null;
        }

        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(dto.getOwner())
                .build();
    }

    public static Item updateItem(Item existingItem, ItemRequestDto updateDto) {
        if (existingItem == null || updateDto == null) {
            return existingItem;
        }

        if (updateDto.getName() != null && !updateDto.getName().isBlank()) {
            existingItem.setName(updateDto.getName());
        }

        if (updateDto.getDescription() != null && !updateDto.getDescription().isBlank()) {
            existingItem.setDescription(updateDto.getDescription());
        }

        if (updateDto.getAvailable() != null) {
            existingItem.setAvailable(updateDto.getAvailable());
        }

        return existingItem;
    }
}