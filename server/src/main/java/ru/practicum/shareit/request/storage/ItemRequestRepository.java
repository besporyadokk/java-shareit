package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {

    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(Integer requesterId);

    @Query("SELECT ir FROM ItemRequest ir " +
            "WHERE ir.requester.id != :userId " +
            "ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequesterIdNot(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT ir FROM ItemRequest ir " +
            "JOIN FETCH ir.requester " +
            "LEFT JOIN FETCH ir.items " +
            "WHERE ir.id = :requestId")
    ItemRequest findByIdWithRequesterAndItems(@Param("requestId") Integer requestId);

}