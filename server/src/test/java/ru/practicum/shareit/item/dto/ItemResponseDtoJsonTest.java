package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    @DisplayName("Сериализация ItemResponseDto в JSON")
    void serializeItemResponseDto() throws IOException {
        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("Great item!")
                .authorName("User")
                .build();

        ItemResponseDto itemDto = ItemResponseDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .comments(List.of(commentDto))
                .build();

        JsonContent<ItemResponseDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathArrayValue("$.comments");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemResponseDto")
    void deserializeItemResponseDto() throws IOException {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true,\"comments\":[]}";

        ItemResponseDto itemDto = json.parseObject(content);

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful drill");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getComments()).isEmpty();
    }
}