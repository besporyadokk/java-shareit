package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto createItemRequest(
            @RequestBody ItemRequestCreateDto itemRequestCreateDto,
            @RequestHeader("X-Sharer-User-Id") Integer requesterId) {
        return itemRequestService.createItemRequest(itemRequestCreateDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer requesterId) {
        return itemRequestService.getOwnItemRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllItemRequests(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequestById(
            @PathVariable Integer requestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemRequestService.getItemRequestById(requestId, userId);
    }
}