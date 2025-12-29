package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User create(User user);
    Optional<User> findById(int id);
    List<User> findAll();
    User update(User user);
    void deleteById(int id);
    boolean existsById(int id);
}
