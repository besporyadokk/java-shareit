package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemResponseDto createItem(ItemRequestDto itemDto, Integer ownerId) {
        validateItemFields(itemDto);

        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        Item item = ItemDtoMapper.toItem(itemDto);
        item.setOwner(owner);

        Item createdItem = itemRepository.save(item);
        log.info("Создана вещь: {}", createdItem);

        return ItemDtoMapper.toResponseDto(createdItem, Collections.emptyList());
    }

    @Override
    @Transactional
    public ItemResponseDto updateItem(Integer itemId, ItemRequestDto updateDto, Integer ownerId) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Только владелец может редактировать вещь");
        }

        Item updatedItem = ItemDtoMapper.updateItem(existingItem, updateDto);
        itemRepository.save(updatedItem);
        log.info("Обновлена вещь с id={}", itemId);

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemDtoMapper.toResponseDto(updatedItem, commentDtos);
    }

    @Override
    public ItemOwnerDto getItemByIdForOwner(Integer itemId, Integer userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        LocalDateTime now = LocalDateTime.now();

        List<Booking> lastBookings = bookingRepository.findLastBookingForItem(
                itemId, BookingStatus.APPROVED, now, PageRequest.of(0, 1));
        List<Booking> nextBookings = bookingRepository.findNextBookingForItem(
                itemId, BookingStatus.APPROVED, now, PageRequest.of(0, 1));

        BookingForItemDto lastBookingDto = lastBookings.isEmpty() ? null :
                BookingMapper.toBookingForItemDto(lastBookings.get(0));
        BookingForItemDto nextBookingDto = nextBookings.isEmpty() ? null :
                BookingMapper.toBookingForItemDto(nextBookings.get(0));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        if (!item.getOwner().getId().equals(userId)) {
            lastBookingDto = null;
            nextBookingDto = null;
        }

        return ItemDtoMapper.toOwnerDto(item, lastBookingDto, nextBookingDto, commentDtos);
    }

    @Override
    public ItemResponseDto getItemById(Integer itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        List<Comment> comments = commentRepository.findByItemId(itemId);
        List<CommentDto> commentDtos = comments.stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        return ItemDtoMapper.toResponseDto(item, commentDtos);
    }

    @Override
    public List<ItemOwnerDto> getAllItemsByOwner(Integer ownerId) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        LocalDateTime now = LocalDateTime.now();
        List<Item> items = itemRepository.findByOwnerId(ownerId);

        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> allComments = commentRepository.findByItemIds(itemIds);

        Map<Integer, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        return items.stream().map(item -> {
            List<Booking> lastBookings = bookingRepository.findLastBookingForItem(
                    item.getId(), BookingStatus.APPROVED, now, PageRequest.of(0, 1));
            List<Booking> nextBookings = bookingRepository.findNextBookingForItem(
                    item.getId(), BookingStatus.APPROVED, now, PageRequest.of(0, 1));

            BookingForItemDto lastBookingDto = lastBookings.isEmpty() ? null :
                    BookingMapper.toBookingForItemDto(lastBookings.get(0));
            BookingForItemDto nextBookingDto = nextBookings.isEmpty() ? null :
                    BookingMapper.toBookingForItemDto(nextBookings.get(0));

            List<CommentDto> commentDtos = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());

            return ItemDtoMapper.toOwnerDto(item, lastBookingDto, nextBookingDto, commentDtos);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchAvailableItems(String text, Integer userId) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        List<Item> items = itemRepository.searchAvailableItems(text.toLowerCase());
        log.info("Найдено {} доступных вещей по запросу '{}'", items.size(), text);

        List<Integer> itemIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());

        List<Comment> allComments = commentRepository.findByItemIds(itemIds);

        Map<Integer, List<CommentDto>> commentsByItemId = allComments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(CommentMapper::toDto, Collectors.toList())
                ));

        return items.stream().map(item -> {
            List<CommentDto> commentDtos = commentsByItemId.getOrDefault(item.getId(), Collections.emptyList());
            return ItemDtoMapper.toResponseDto(item, commentDtos);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteItem(Integer itemId, Integer ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new AccessDeniedException("Только владелец может удалить вещь");
        }

        itemRepository.deleteById(itemId);
        log.info("Удалена вещь с id={}", itemId);
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer itemId, Integer userId, CommentRequestDto commentRequestDto) {
        validateComment(commentRequestDto);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        List<Booking> bookings = bookingRepository.findBookingsForComment(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());

        if (bookings.isEmpty()) {
            throw new BadRequestException("Пользователь не бронировал эту вещь или бронирование еще не завершено");
        }

        Comment comment = Comment.builder()
                .text(commentRequestDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);
        log.info("Добавлен комментарий к вещи с id={} от пользователя с id={}", itemId, userId);

        return CommentMapper.toDto(savedComment);
    }

    public void validateItemFields(ItemRequestDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("Название вещи не может быть пустым");
        }

        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new ValidationException("Описание вещи не может быть пустым");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Поле 'available' обязательно для заполнения");
        }
    }

    private void validateComment(CommentRequestDto commentRequestDto) {
        if (commentRequestDto.getText() == null || commentRequestDto.getText().isBlank()) {
            throw new ValidationException("Текст комментария не может быть пустым");
        }
    }
}