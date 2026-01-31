package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemResponseDto> createItem(@RequestBody ItemRequestDto itemDto,
                                                      @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        ItemResponseDto createdItem = itemService.createItem(itemDto, ownerId);
        return ResponseEntity.ok(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> updateItem(@PathVariable Integer itemId,
                                                      @RequestBody ItemRequestDto updateDto,
                                                      @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        ItemResponseDto updatedItem = itemService.updateItem(itemId, updateDto, ownerId);
        return ResponseEntity.ok(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemOwnerDto> getItemByIdForOwner(@PathVariable Integer itemId,
                                                            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        ItemOwnerDto item = itemService.getItemByIdForOwner(itemId, userId);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/simple/{itemId}")
    public ResponseEntity<ItemResponseDto> getItemById(@PathVariable Integer itemId) {
        ItemResponseDto item = itemService.getItemById(itemId);
        return ResponseEntity.ok(item);
    }

    @GetMapping
    public ResponseEntity<List<ItemOwnerDto>> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        List<ItemOwnerDto> items = itemService.getAllItemsByOwner(ownerId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemResponseDto>> searchItems(@RequestParam String text,
                                                             @RequestHeader("X-Sharer-User-Id") Integer userId) {
        List<ItemResponseDto> items = itemService.searchAvailableItems(text, userId);
        return ResponseEntity.ok(items);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer itemId,
                                           @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        itemService.deleteItem(itemId, ownerId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@PathVariable Integer itemId,
                                                 @RequestBody CommentRequestDto commentRequestDto,
                                                 @RequestHeader("X-Sharer-User-Id") Integer userId) {
        CommentDto comment = itemService.addComment(itemId, userId, commentRequestDto);
        return ResponseEntity.ok(comment);
    }
}