package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto createItem(ItemDto itemDto, int ownerId) {

        validateItemFields(itemDto);

        var owner = userStorage.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + ownerId + " не найден"));

        itemDto.setOwner(owner);

        Item item = ItemDtoMapper.toItem(itemDto);
        Item createdItem = itemStorage.create(item);
        return ItemDtoMapper.toDto(createdItem);
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto updateDto, int ownerId) {
        Item existingItem = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (existingItem.getOwner() == null || existingItem.getOwner().getId() != ownerId) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        Item updatedItem = ItemDtoMapper.updateItem(existingItem, updateDto);
        itemStorage.update(updatedItem);

        return ItemDtoMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItemById(int itemId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));
        return ItemDtoMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(int ownerId) {
        return itemStorage.findByOwnerId(ownerId).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.searchAvailableItems(text).stream()
                .map(ItemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteItem(int itemId, int ownerId) {
        Item item = itemStorage.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id=" + itemId + " не найдена"));

        if (item.getOwner() == null || item.getOwner().getId() != ownerId) {
            throw new NotFoundException("Только владелец может удалить вещь");
        }

        itemStorage.deleteById(itemId);
    }

    @Override
    public void validateItemFields(ItemDto itemDto) {
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
}