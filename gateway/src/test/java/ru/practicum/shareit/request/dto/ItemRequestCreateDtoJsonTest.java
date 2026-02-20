package ru.practicum.shareit.request.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestCreateDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestCreateDto> json;

    private final Validator validator;

    public ItemRequestCreateDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Сериализация ItemRequestCreateDto в JSON")
    void serializeItemRequestCreateDto() throws IOException {
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();

        JsonContent<ItemRequestCreateDto> result = json.write(requestDto);

        assertThat(result).hasJsonPathStringValue("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Need a drill");
    }

    @Test
    @DisplayName("Десериализация JSON в ItemRequestCreateDto")
    void deserializeItemRequestCreateDto() throws IOException {
        String content = "{\"description\":\"Need a drill\"}";

        ItemRequestCreateDto requestDto = json.parseObject(content);

        assertThat(requestDto.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    @DisplayName("Валидация - успешный случай")
    void validateValidRequestDto() {
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(requestDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Валидация - пустое описание")
    void validateBlankDescription() {
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(requestDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Валидация - описание слишком длинное")
    void validateDescriptionTooLong() {
        String longDescription = "a".repeat(1001);
        ItemRequestCreateDto requestDto = ItemRequestCreateDto.builder()
                .description(longDescription)
                .build();

        Set<ConstraintViolation<ItemRequestCreateDto>> violations = validator.validate(requestDto);
        assertThat(violations).isNotEmpty();
    }
}