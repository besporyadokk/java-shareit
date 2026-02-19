package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingResponseStatus;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private BookingRepository bookingRepository;

    private Integer ownerId;
    private Integer bookerId;
    private Integer itemId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();

        // Создаем владельца
        UserResponseDto owner = new UserResponseDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        ownerId = userService.create(owner).getId();

        // Создаем бронирующего
        UserResponseDto booker = new UserResponseDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        bookerId = userService.create(booker).getId();

        // Создаем вещь
        ItemRequestDto itemDto = ItemRequestDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
        itemId = itemService.createItem(itemDto, ownerId).getId();
    }

    private BookingRequestDto createTestBookingDto() {
        return BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    @DisplayName("Создание бронирования")
    void createBooking_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        BookingResponseDto savedBooking = bookingService.createBooking(bookingDto, bookerId);

        assertThat(savedBooking).isNotNull();
        assertThat(savedBooking.getId()).isNotNull();
        assertThat(savedBooking.getStatus()).isEqualTo(BookingResponseStatus.WAITING);
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void approveBooking_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        BookingResponseDto savedBooking = bookingService.createBooking(bookingDto, bookerId);

        BookingResponseDto approvedBooking = bookingService.approveBooking(savedBooking.getId(), true, ownerId);

        assertThat(approvedBooking.getStatus()).isEqualTo(BookingResponseStatus.APPROVED);
    }

    @Test
    @DisplayName("Отклонение бронирования")
    void rejectBooking_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        BookingResponseDto savedBooking = bookingService.createBooking(bookingDto, bookerId);

        BookingResponseDto rejectedBooking = bookingService.approveBooking(savedBooking.getId(), false, ownerId);

        assertThat(rejectedBooking.getStatus()).isEqualTo(BookingResponseStatus.REJECTED);
    }

    @Test
    @DisplayName("Получение бронирования по ID")
    void getBookingById_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        BookingResponseDto savedBooking = bookingService.createBooking(bookingDto, bookerId);

        BookingResponseDto retrievedBooking = bookingService.getBookingById(savedBooking.getId(), bookerId);

        assertThat(retrievedBooking).isNotNull();
        assertThat(retrievedBooking.getId()).isEqualTo(savedBooking.getId());
    }

    @Test
    @DisplayName("Получение несуществующего бронирования")
    void getBookingById_NotFound_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(999, bookerId));
    }

    @Test
    @DisplayName("Получение бронирований пользователя")
    void getUserBookings_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        bookingService.createBooking(bookingDto, bookerId);

        var bookings = bookingService.getUserBookings(bookerId, BookingState.ALL, 0, 10);

        assertThat(bookings).isNotEmpty();
    }

    @Test
    @DisplayName("Получение бронирований владельца")
    void getOwnerBookings_Success() {
        BookingRequestDto bookingDto = createTestBookingDto();
        bookingService.createBooking(bookingDto, bookerId);

        var bookings = bookingService.getOwnerBookings(ownerId, BookingState.ALL, 0, 10);

        assertThat(bookings).isNotEmpty();
    }
}