package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto updateDto, int ownerId);

    ItemDto getItemById(int itemId);

    List<ItemDto> getAllItemsByOwner(int ownerId);

    List<ItemDto> searchAvailableItems(String text);

    void deleteItem(int itemId, int ownerId);

    void validateItemFields(ItemDto itemDto);
}
