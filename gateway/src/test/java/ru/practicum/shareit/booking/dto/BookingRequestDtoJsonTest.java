package ru.practicum.shareit.booking.dto;

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
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingRequestDtoJsonTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    private final Validator validator;

    public BookingRequestDtoJsonTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    @DisplayName("Сериализация BookingRequestDto в JSON")
    void serializeBookingRequestDto() throws IOException {
        LocalDateTime start = LocalDateTime.of(2025, 1, 1, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 1, 2, 10, 0);

        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(1)
                .start(start)
                .end(end)
                .build();

        JsonContent<BookingRequestDto> result = json.write(bookingDto);

        assertThat(result).hasJsonPathNumberValue("$.itemId");
        assertThat(result).hasJsonPathStringValue("$.start");
        assertThat(result).hasJsonPathStringValue("$.end");
    }

    @Test
    @DisplayName("Десериализация JSON в BookingRequestDto")
    void deserializeBookingRequestDto() throws IOException {
        String content = "{\"itemId\":1,\"start\":\"2025-01-01T10:00:00\",\"end\":\"2025-01-02T10:00:00\"}";

        BookingRequestDto bookingDto = json.parseObject(content);

        assertThat(bookingDto.getItemId()).isEqualTo(1);
        assertThat(bookingDto.getStart()).isNotNull();
        assertThat(bookingDto.getEnd()).isNotNull();
    }

    @Test
    @DisplayName("Валидация - успешный случай")
    void validateValidBookingDto() {
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Валидация - null itemId")
    void validateNullItemId() {
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Валидация - null start")
    void validateNullStart() {
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(1)
                .start(null)
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Валидация - start в прошлом")
    void validateStartInPast() {
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingRequestDto>> violations = validator.validate(bookingDto);
        assertThat(violations).isNotEmpty();
    }
}