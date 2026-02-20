package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BookingStatusTest {

    @Test
    void testEnumValues() {
        assertThat(BookingStatus.values()).hasSize(4);
        assertThat(BookingStatus.valueOf("WAITING")).isEqualTo(BookingStatus.WAITING);
        assertThat(BookingStatus.valueOf("APPROVED")).isEqualTo(BookingStatus.APPROVED);
        assertThat(BookingStatus.valueOf("REJECTED")).isEqualTo(BookingStatus.REJECTED);
        assertThat(BookingStatus.valueOf("CANCELED")).isEqualTo(BookingStatus.CANCELED);
    }
}