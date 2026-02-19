package ru.practicum.shareit.booking.dto;

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
class BookingForItemDtoJsonTest {

    @Autowired
    private JacksonTester<BookingForItemDto> json;

    @Test
    @DisplayName("Сериализация BookingForItemDto в JSON")
    void serializeBookingForItemDto() throws IOException {
        BookingForItemDto bookingDto = BookingForItemDto.builder()
                .id(1)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 2, 10, 0))
                .bookerId(2)
                .build();

        JsonContent<BookingForItemDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
        assertThat(result).hasJsonPathNumberValue("$.bookerId");
    }

    @Test
    @DisplayName("Десериализация JSON в BookingForItemDto")
    void deserializeBookingForItemDto() throws IOException {
        String content = "{\"id\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\",\"bookerId\":2}";

        BookingForItemDto bookingDto = json.parseObject(content);

        assertThat(bookingDto.getId()).isEqualTo(1);
        assertThat(bookingDto.getBookerId()).isEqualTo(2);
    }
}