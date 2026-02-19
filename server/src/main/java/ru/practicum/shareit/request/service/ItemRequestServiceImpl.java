package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseForRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, Integer requesterId) {
        validateItemRequestCreateDto(itemRequestCreateDto);

        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requesterId + " не найден"));

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestCreateDto.getDescription())
                .requester(requester)
                .created(LocalDateTime.now())
                .build();

        ItemRequest savedRequest = itemRequestRepository.save(itemRequest);
        log.info("Создан запрос на вещь с id={} от пользователя с id={}", savedRequest.getId(), requesterId);

        return ItemRequestMapper.toDto(savedRequest, Collections.emptyList());
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Integer requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + requesterId + " не найден"));

        List<ItemRequest> itemRequests = itemRequestRepository.findByRequesterIdOrderByCreatedDesc(requesterId);

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
                    List<ItemResponseForRequestDto> itemDtos = items.stream()
                            .map(ItemRequestMapper::toItemResponseForRequestDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toDto(itemRequest, itemDtos);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer userId, Integer from, Integer size) {
        validatePaginationParameters(from, size);

        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        Pageable pageable = PageRequest.of(from / size, size);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdNot(userId, pageable);

        return itemRequests.stream()
                .map(itemRequest -> {
                    List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
                    List<ItemResponseForRequestDto> itemDtos = items.stream()
                            .map(ItemRequestMapper::toItemResponseForRequestDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toDto(itemRequest, itemDtos);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer requestId, Integer userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на вещь с id=" + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ItemResponseForRequestDto> itemDtos = items.stream()
                .map(ItemRequestMapper::toItemResponseForRequestDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toDto(itemRequest, itemDtos);
    }

    private void validateItemRequestCreateDto(ItemRequestCreateDto itemRequestCreateDto) {
        if (itemRequestCreateDto.getDescription() == null || itemRequestCreateDto.getDescription().isBlank()) {
            throw new ValidationException("Описание запроса не может быть пустым");
        }
    }

    private void validatePaginationParameters(Integer from, Integer size) {
        if (from == null || from < 0) {
            throw new ValidationException("Параметр 'from' должен быть неотрицательным");
        }

        if (size == null || size <= 0) {
            throw new ValidationException("Параметр 'size' должен быть положительным");
        }
    }
}