package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findByOwnerId(int ownerId);

    @Query("SELECT i FROM Item i WHERE i.available = true AND " +
            "(LOWER(i.name) LIKE %:text% OR LOWER(i.description) LIKE %:text%)")
    List<Item> searchAvailableItems(String text);

    List<Item> findByRequestId(Integer requestId);

    List<Item> findByRequestIdIn(List<Integer> requestIds);
}