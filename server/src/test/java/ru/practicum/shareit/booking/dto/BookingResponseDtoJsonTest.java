package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingResponseStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    @DisplayName("Сериализация BookingResponseDto в JSON")
    void serializeBookingResponseDto() throws IOException {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(2);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        BookingResponseDto bookingDto = BookingResponseDto.builder()
                .id(1)
                .start(LocalDateTime.of(2025, 1, 1, 10, 0))
                .end(LocalDateTime.of(2025, 1, 2, 10, 0))
                .status(BookingResponseStatus.APPROVED)
                .booker(userDto)
                .item(itemDto)
                .build();

        JsonContent<BookingResponseDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.status");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.item");
    }

    @Test
    @DisplayName("Десериализация JSON в BookingResponseDto")
    void deserializeBookingResponseDto() throws IOException {
        String content = "{\"id\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\",\"status\":\"APPROVED\"}";

        BookingResponseDto bookingDto = json.parseObject(content);

        assertThat(bookingDto.getId()).isEqualTo(1);
        assertThat(bookingDto.getStatus()).isEqualTo(BookingResponseStatus.APPROVED);
    }
}