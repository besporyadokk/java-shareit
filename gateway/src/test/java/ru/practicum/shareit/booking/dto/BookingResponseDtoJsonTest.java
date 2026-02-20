package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingResponseDtoJsonTest {

    @Autowired
    private JacksonTester<BookingResponseDto> json;

    @Test
    void testSerialize() throws Exception {
        UserDto user = new UserDto();
        user.setId(1);
        user.setName("User");
        user.setEmail("user@test.com");

        ItemDto item = ItemDto.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        BookingResponseDto dto = BookingResponseDto.builder()
                .id(1)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(user)
                .item(item)
                .build();

        assertThat(json.write(dto)).hasJsonPath("$.id");
    }
}