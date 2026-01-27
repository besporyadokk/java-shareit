package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

public class CommentMapper {

    public static CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                .created(comment.getCreated())
                .build();
    }

    public static Comment toComment(String text, ru.practicum.shareit.item.model.Item item,
                                    ru.practicum.shareit.user.model.User author) {
        return Comment.builder()
                .text(text)
                .item(item)
                .author(author)
                .created(java.time.LocalDateTime.now())
                .build();
    }
}