package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemStorage;
    private final UserRepository userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemResponseDto createItem(ItemRequestDto itemDto, Integer ownerId) {
        validateItemFields(itemDto);

        var owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        itemDto.setOwner(owner);

        Item item = ItemDtoMapper.toItem(itemDto);
        Item createdItem = itemStorage.save(item);
        return ItemDtoMapper.toResponseDto(createdItem, commentRepository);
    }

    @Override
    public ItemResponseDto updateItem(Integer itemId, ItemRequestDto updateDto, Integer ownerId) {
        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (existingItem.getOwner() == null || !existingItem.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        Item updatedItem = ItemDtoMapper.updateItem(existingItem, updateDto);
        itemStorage.save(updatedItem);

        return ItemDtoMapper.toResponseDto(updatedItem, commentRepository);
    }

    @Override
    public ItemOwnerDto getItemByIdForOwner(Integer itemId, Integer userId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (item.getOwner() == null || !item.getOwner().getId().equals(userId)) {
            throw new AccessDeniedException("Только владелец может видеть информацию о бронированиях");
        }

        return ItemDtoMapper.toOwnerDto(item, bookingRepository, commentRepository);
    }

    @Override
    public ItemResponseDto getItemById(Integer itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        return ItemDtoMapper.toResponseDto(item, commentRepository);
    }

    @Override
    public List<ItemOwnerDto> getAllItemsByOwner(Integer ownerId) {
        userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        return itemStorage.findByOwnerId(ownerId).stream()
                .map(item -> ItemDtoMapper.toOwnerDto(item, bookingRepository, commentRepository))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemResponseDto> searchAvailableItems(String text, Integer userId) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String searchText = text.toLowerCase();

        return itemStorage.searchAvailableItems(searchText).stream()
                .map(item -> ItemDtoMapper.toResponseDto(item, commentRepository))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(Integer itemId, Integer ownerId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (item.getOwner() == null || !item.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Только владелец может удалить вещь");
        }

        itemStorage.deleteById(itemId);
    }

    @Override
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

    @Transactional
    public CommentDto addComment(Integer itemId, Integer userId, CommentRequestDto commentRequestDto) {
        User author = userStorage.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        boolean hasBooking = bookingRepository.findBookingsForComment(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now()).size() > 0;

        if (!hasBooking) {
            throw new BadRequestException("Вы не можете оставить отзыв на вещь, которую не брали в аренду");
        }

        if (commentRepository.existsByAuthorIdAndItemId(userId, itemId)) {
            throw new BadRequestException("Вы уже оставляли отзыв на эту вещь");
        }

        Comment comment = CommentMapper.toComment(commentRequestDto.getText(), item, author);
        Comment savedComment = commentRepository.save(comment);

        return CommentMapper.toDto(savedComment);
    }
}