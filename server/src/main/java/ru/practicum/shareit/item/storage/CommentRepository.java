package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByItemId(Integer itemId);

    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.author " +
            "WHERE c.item.id IN :itemIds " +
            "ORDER BY c.created DESC")
    List<Comment> findByItemIds(@Param("itemIds") List<Integer> itemIds);

    @Query("SELECT COUNT(c) > 0 FROM Comment c " +
            "WHERE c.author.id = :authorId AND c.item.id = :itemId")
    boolean existsByAuthorIdAndItemId(@Param("authorId") Integer authorId,
                                      @Param("itemId") Integer itemId);
}