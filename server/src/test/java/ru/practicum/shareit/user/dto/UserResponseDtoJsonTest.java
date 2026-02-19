package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserResponseDtoJsonTest {

    @Autowired
    private JacksonTester<UserResponseDto> json;

    @Test
    @DisplayName("Сериализация UserResponseDto в JSON")
    void serializeUserResponseDto() throws IOException {
        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(1);
        userDto.setName("John Doe");
        userDto.setEmail("john@example.com");

        JsonContent<UserResponseDto> result = json.write(userDto);

        assertThat(result).hasJsonPathNumberValue("$.id");
        assertThat(result).hasJsonPathStringValue("$.name");
        assertThat(result).hasJsonPathStringValue("$.email");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("John Doe");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Десериализация JSON в UserResponseDto")
    void deserializeUserResponseDto() throws IOException {
        String content = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserResponseDto userDto = json.parseObject(content);

        assertThat(userDto.getId()).isEqualTo(1);
        assertThat(userDto.getName()).isEqualTo("John Doe");
        assertThat(userDto.getEmail()).isEqualTo("john@example.com");
    }
}