package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemServiceImpl itemServiceImpl;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemServiceImpl.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(
            @PathVariable int itemId,
            @RequestBody ItemDto updateDto,
            @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemServiceImpl.updateItem(itemId, updateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@PathVariable int itemId) {
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") int ownerId) {
        return itemServiceImpl.getAllItemsByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(
            @RequestParam String text) {
        return itemServiceImpl.searchAvailableItems(text);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable int itemId,
            @RequestHeader("X-Sharer-User-Id") int ownerId) {
        itemServiceImpl.deleteItem(itemId, ownerId);
    }
}
