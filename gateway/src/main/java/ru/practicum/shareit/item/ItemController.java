package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemRequestDto itemDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Creating item: {}, ownerId={}", itemDto, ownerId);
        return itemClient.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(
            @PathVariable Integer itemId,
            @RequestBody ItemRequestDto updateDto,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Updating item: itemId={}, updateDto={}, ownerId={}", itemId, updateDto, ownerId);
        return itemClient.update(itemId, updateDto, ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getByIdForOwner(
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Getting item for owner: itemId={}, userId={}", itemId, userId);
        return itemClient.getByIdForOwner(itemId, userId);
    }

    @GetMapping("/simple/{itemId}")
    public ResponseEntity<Object> getById(@PathVariable Integer itemId) {
        log.info("Getting simple item: itemId={}", itemId);
        return itemClient.getById(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByOwner(
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Getting all items by owner: ownerId={}", ownerId);
        return itemClient.getAllByOwner(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(
            @RequestParam String text,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Searching items: text={}, userId={}", text, userId);
        return itemClient.search(text, userId);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(
            @PathVariable Integer itemId,
            @RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Deleting item: itemId={}, ownerId={}", itemId, ownerId);
        return itemClient.delete(itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(
            @PathVariable Integer itemId,
            @Valid @RequestBody CommentRequestDto commentRequestDto,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Adding comment: itemId={}, userId={}, comment={}", itemId, userId, commentRequestDto);
        return itemClient.addComment(itemId, userId, commentRequestDto);
    }
}