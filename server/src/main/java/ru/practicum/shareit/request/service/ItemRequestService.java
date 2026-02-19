package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Integer requesterId);

    List<ItemRequestDto> getOwnItemRequests(Integer requesterId);

    List<ItemRequestDto> getAllItemRequests(Integer userId, Integer from, Integer size);

    ItemRequestDto getItemRequestById(Integer requestId, Integer userId);
}