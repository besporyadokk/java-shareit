package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @DisplayName("Сериализация CommentDto в JSON")
    void serializeCommentDto() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("Great item!")
                .authorName("John Doe")
                .created(LocalDateTime.of(2025, 1, 1, 10, 0))
                .build();

        JsonContent<CommentDto> result = json.write(commentDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).hasJsonPathStringValue("$.authorName");
        assertThat(result).hasJsonPathStringValue("$.created");
    }

    @Test
    @DisplayName("Десериализация JSON в CommentDto")
    void deserializeCommentDto() throws IOException {
        String content = "{\"id\":1,\"text\":\"Great item!\",\"authorName\":\"John Doe\",\"created\":\"2025-01-01T10:00:00\"}";

        CommentDto commentDto = json.parseObject(content);

        assertThat(commentDto.getId()).isEqualTo(1);
        assertThat(commentDto.getText()).isEqualTo("Great item!");
        assertThat(commentDto.getAuthorName()).isEqualTo("John Doe");
        assertThat(commentDto.getCreated()).isNotNull();
    }
}