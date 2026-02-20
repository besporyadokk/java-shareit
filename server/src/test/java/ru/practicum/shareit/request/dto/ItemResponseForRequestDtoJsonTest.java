package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseForRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemResponseForRequestDto> json;

    @Test
    @DisplayName("Сериализация ItemResponseForRequestDto в JSON")
    void serializeItemResponseForRequestDto() throws IOException {
        ItemResponseForRequestDto itemDto = ItemResponseForRequestDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .ownerId(2)
                .requestId(1)
                .build();

        JsonContent<ItemResponseForRequestDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathBooleanValue("$.available");
        assertThat(result).hasJsonPathNumberValue("$.ownerId");
        assertThat(result).hasJsonPathNumberValue("$.requestId");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemResponseForRequestDto")
    void deserializeItemResponseForRequestDto() throws IOException {
        String content = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill\",\"available\":true,\"ownerId\":2,\"requestId\":1}";

        ItemResponseForRequestDto itemDto = json.parseObject(content);

        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Drill");
        assertThat(itemDto.getDescription()).isEqualTo("Powerful drill");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getOwnerId()).isEqualTo(2);
        assertThat(itemDto.getRequestId()).isEqualTo(1);
    }
}