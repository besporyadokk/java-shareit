package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    void testSerialize() throws Exception {
        ItemDto dto = ItemDto.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .build();

        assertThat(json.write(dto)).hasJsonPath("$.id");
        assertThat(json.write(dto)).hasJsonPath("$.name");
        assertThat(json.write(dto)).hasJsonPath("$.available");
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"id\":1,\"name\":\"Item\",\"description\":\"Description\",\"available\":true}";

        ItemDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Item");
    }
}