package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BookingStateTest {

    @Test
    void testEnumValues() {
        assertThat(BookingState.values()).hasSize(6);
        assertThat(BookingState.valueOf("ALL")).isEqualTo(BookingState.ALL);
        assertThat(BookingState.valueOf("CURRENT")).isEqualTo(BookingState.CURRENT);
        assertThat(BookingState.valueOf("PAST")).isEqualTo(BookingState.PAST);
        assertThat(BookingState.valueOf("FUTURE")).isEqualTo(BookingState.FUTURE);
        assertThat(BookingState.valueOf("WAITING")).isEqualTo(BookingState.WAITING);
        assertThat(BookingState.valueOf("REJECTED")).isEqualTo(BookingState.REJECTED);
    }
}