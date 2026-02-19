package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @DisplayName("Сериализация ItemDto в JSON")
    void serializeItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        JsonContent<ItemDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemDto")
    void deserializeItemDto() throws IOException {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true}";

        ItemDto itemDto = json.parseObject(content);

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful drill");
        assertThat(itemDto.getAvailable()).isTrue();
    }
}