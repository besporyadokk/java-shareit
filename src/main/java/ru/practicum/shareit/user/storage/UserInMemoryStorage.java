package ru.practicum.shareit.user.storage;


import ru.practicum.shareit.user.model.User;

import java.util.*;
import org.springframework.stereotype.Repository;

@Repository
public class UserInMemoryStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        if (user.getId() == 0) {
            user.setId(nextId++);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
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
    public void deleteById(int id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(int id) {
        return users.containsKey(id);
    }

}