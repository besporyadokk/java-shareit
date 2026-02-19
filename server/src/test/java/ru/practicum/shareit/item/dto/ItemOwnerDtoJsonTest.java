package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOwnerDtoJsonTest {

    @Autowired
    private JacksonTester<ItemOwnerDto> json;

    @Test
    @DisplayName("Сериализация ItemOwnerDto в JSON")
    void serializeItemOwnerDto() throws IOException {
        BookingForItemDto lastBooking = BookingForItemDto.builder()
                .id(1)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 2, 10, 0))
                .bookerId(2)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .text("Great item!")
                .authorName("User")
                .build();

        ItemOwnerDto itemDto = ItemOwnerDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(null)
                .comments(List.of(commentDto))
                .build();

        JsonContent<ItemOwnerDto> result = json.write(itemDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPath("$.lastBooking");
        assertThat(result).hasJsonPathArrayValue("$.comments");
    }
}