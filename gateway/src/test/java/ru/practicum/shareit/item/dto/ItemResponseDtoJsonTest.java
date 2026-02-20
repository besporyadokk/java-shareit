package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemResponseDtoJsonTest {

    @Autowired
    private JacksonTester<ItemResponseDto> json;

    @Test
    void testSerialize() throws Exception {
        CommentDto comment = CommentDto.builder()
                .id(1)
                .text("Great!")
                .authorName("User")
                .build();

        ItemResponseDto dto = ItemResponseDto.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .comments(List.of(comment))
                .build();

        assertThat(json.write(dto)).hasJsonPath("$.id");
    }
}