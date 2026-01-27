package ru.practicum.shareit.item.storage;


import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

public class ItemInMemoryStorage implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    private Integer idCounter = 1;

    @Override
    public Item create(Item item) {
        if (item.getId() == 0) {
            item.setId(idCounter++);
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Integer id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findByOwnerId(Integer ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> searchAvailableItems(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerCaseText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> {
                    String name = item.getName() != null ? item.getName().toLowerCase() : "";
                    String description = item.getDescription() != null ? item.getDescription().toLowerCase() : "";
                    return name.contains(lowerCaseText) || description.contains(lowerCaseText);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Item update(Item item) {
        if (items.containsKey(item.getId())) {
            items.put(item.getId(), item);
            return item;
        }
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        items.remove(id);
    }
}