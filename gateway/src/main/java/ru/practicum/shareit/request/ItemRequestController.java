package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @Valid @RequestBody ItemRequestCreateDto itemRequestCreateDto,
            @RequestHeader("X-Sharer-User-Id") Integer requesterId) {
        log.info("Creating item request: {}, requesterId={}", itemRequestCreateDto, requesterId);
        return itemRequestClient.create(itemRequestCreateDto, requesterId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwn(
            @RequestHeader("X-Sharer-User-Id") Integer requesterId) {
        log.info("Getting own item requests: requesterId={}", requesterId);
        return itemRequestClient.getOwn(requesterId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @RequestHeader("X-Sharer-User-Id") Integer userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all item requests: userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(
            @PathVariable Integer requestId,
            @RequestHeader("X-Sharer-User-Id") Integer userId) {
        log.info("Getting item request by id: requestId={}, userId={}", requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }
}