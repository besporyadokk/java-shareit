package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingForItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemOwnerDtoJsonTest {

    @Autowired
    private JacksonTester<ItemOwnerDto> json;

    @Test
    void testSerialize() throws Exception {
        BookingForItemDto lastBooking = BookingForItemDto.builder()
                .id(1)
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .bookerId(2)
                .build();

        CommentDto comment = CommentDto.builder()
                .id(1)
                .text("Great!")
                .authorName("User")
                .build();

        ItemOwnerDto dto = ItemOwnerDto.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .lastBooking(lastBooking)
                .nextBooking(null)
                .comments(List.of(comment))
                .build();

        assertThat(json.write(dto)).hasJsonPath("$.id");
        assertThat(json.write(dto)).hasJsonPath("$.lastBooking");
    }
}