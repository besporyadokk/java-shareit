package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @DisplayName("Сериализация ItemRequestDto в JSON")
    void serializeItemRequestDto() throws IOException {
        ItemResponseForRequestDto itemDto = ItemResponseForRequestDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .ownerId(2)
                .requestId(1)
                .build();

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1)
                .description("Need a drill")
                .created(LocalDateTime.of(2025, 1, 1, 10, 0))
                .items(List.of(itemDto))
                .build();

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).hasJsonPathStringValue("$.created");
        assertThat(result).hasJsonPathArrayValue("$.items");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemRequestDto")
    void deserializeItemRequestDto() throws IOException {
        String content = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2025-01-01T10:00:00\",\"items\":[]}";

        ItemRequestDto requestDto = json.parseObject(content);

        assertThat(requestDto.getId()).isEqualTo(1);
        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
        assertThat(requestDto.getCreated()).isNotNull();
        assertThat(requestDto.getItems()).isEmpty();
    }
}