package ru.practicum.shareit.user.storage;


import ru.practicum.shareit.user.model.User;

import java.util.*;


public class UserInMemoryStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private Integer nextId = 1;

    @Override
    public User create(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Integer id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return users.containsKey(id);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

}