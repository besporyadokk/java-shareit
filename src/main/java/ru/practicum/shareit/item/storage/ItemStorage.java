package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Optional<Item> findById(int id);

    List<Item> findAll();

    List<Item> findByOwnerId(int ownerId);

    List<Item> searchAvailableItems(String text);

    Item update(Item item);

    void deleteById(int id);
}
