package ru.practicum.shareit.item.dto;

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
class CommentRequestDtoJsonTest {

    @Autowired
    private JacksonTester<CommentRequestDto> json;

    private final Validator validator;

    public CommentRequestDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Сериализация CommentRequestDto в JSON")
    void serializeCommentRequestDto() throws IOException {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        JsonContent<CommentRequestDto> result = json.write(commentDto);

        assertThat(result).hasJsonPathStringValue("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Great item!");
    }

    @Test
    @DisplayName("Десериализация JSON в CommentRequestDto")
    void deserializeCommentRequestDto() throws IOException {
        String content = "{\"text\":\"Great item!\"}";

        CommentRequestDto commentDto = json.parseObject(content);

        assertThat(commentDto.getText()).isEqualTo("Great item!");
    }

    @Test
    @DisplayName("Валидация - успешный случай")
    void validateValidCommentDto() {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Валидация - пустой текст")
    void validateBlankText() {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("");

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Валидация - текст слишком длинный")
    void validateTextTooLong() {
        String longText = "a".repeat(1001);
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText(longText);

        Set<ConstraintViolation<CommentRequestDto>> violations = validator.validate(commentDto);
        assertThat(violations).isNotEmpty();
    }
}