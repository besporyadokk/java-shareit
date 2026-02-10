package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemResponseDto createItem(ItemRequestDto itemDto, Integer ownerId);

    ItemResponseDto updateItem(Integer itemId, ItemRequestDto updateDto, Integer ownerId);

    ItemOwnerDto getItemByIdForOwner(Integer itemId, Integer userId);

    ItemResponseDto getItemById(Integer itemId);

    List<ItemOwnerDto> getAllItemsByOwner(Integer ownerId);

    List<ItemResponseDto> searchAvailableItems(String text, Integer userId);

    void deleteItem(Integer itemId, Integer ownerId);

    void validateItemFields(ItemRequestDto itemDto);

    CommentDto addComment(Integer itemId, Integer userId, CommentRequestDto commentRequestDto);
}