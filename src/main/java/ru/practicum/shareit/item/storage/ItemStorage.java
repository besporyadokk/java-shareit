package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Optional<Item> findById(Integer id);

    List<Item> findAll();

    List<Item> findByOwnerId(Integer ownerId);

    List<Item> searchAvailableItems(String text);

    Item update(Item item);

    void deleteById(Integer id);
}
