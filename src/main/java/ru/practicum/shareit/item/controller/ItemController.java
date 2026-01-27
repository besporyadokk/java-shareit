package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;
    private final ItemRepository itemStorage;
    private final CommentRepository commentRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(
            @RequestBody ItemRequestDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemServiceImpl.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(
            @PathVariable Integer itemId,
            @RequestBody ItemRequestDto updateDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemServiceImpl.updateItem(itemId, updateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemOwnerDto getById(
            @PathVariable Integer itemId,
            @RequestHeader(value = "X-Sharer-User-Id", required = false) Integer userId) {

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (userId != null) {
            try {
                // Пробуем получить для владельца
                return itemServiceImpl.getItemByIdForOwner(itemId, userId);
            } catch (AccessDeniedException e) {
                // Если не владелец - возвращаем ItemOwnerDto с null для бронирований
                return ItemOwnerDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .lastBooking(null)
                        .nextBooking(null)
                        .comments(commentRepository.findByItemId(itemId).stream()
                                .map(CommentMapper::toDto)
                                .collect(Collectors.toList()))
                        .build();
            }
        }

        // Для неавторизованных пользователей или если userId не передан
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(null)
                .nextBooking(null)
                .comments(commentRepository.findByItemId(itemId).stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    @GetMapping
    public List<ItemOwnerDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        return itemServiceImpl.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemServiceImpl.searchAvailableItems(text, userId);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        itemServiceImpl.deleteItem(itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestBody CommentRequestDto commentRequestDto) {
        return itemServiceImpl.addComment(itemId, userId, commentRequestDto);
    }
}