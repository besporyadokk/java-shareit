package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

public class ItemDtoMapper {

    public static ItemDto toDto(Item model) {
        return ItemDto.builder()
                .id(model.getId())
                .name(model.getName())
                .description(model.getDescription())
                .available(model.getAvailable())
                .owner(model.getOwner())
                .request(model.getRequest() != null ? model.getRequest() : null)
                .build();
    }

    public static Item toItem(ItemDto dto) {
        return Item.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .available(dto.getAvailable())
                .owner(dto.getOwner())
                .request(dto.getRequest() != null ? dto.getRequest() : null)
                .build();
    }

    public static Item updateItem(Item existingItem, ItemDto updateDto) {

        Item.ItemBuilder builder = Item.builder()
                .id(existingItem.getId())
                .owner(existingItem.getOwner())
                .request(existingItem.getRequest());


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