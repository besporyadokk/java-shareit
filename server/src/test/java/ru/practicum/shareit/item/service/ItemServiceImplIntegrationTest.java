package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {

    @Autowired
    private ItemServiceImpl itemService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private BookingServiceImpl bookingService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Integer ownerId;
    private Integer bookerId;
    private Integer itemId;

    @BeforeEach
    void setUp() {
        // Очищаем все репозитории
        commentRepository.deleteAll();
        bookingRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

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

    private ItemRequestDto createTestItemDto() {
        return ItemRequestDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Создание вещи")
    void createItem_Success() {
        ItemRequestDto itemDto = createTestItemDto();
        ItemResponseDto savedItem = itemService.createItem(itemDto, ownerId);

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isNotNull();
        assertThat(savedItem.getName()).isEqualTo("Test Item");
        assertThat(savedItem.getDescription()).isEqualTo("Test Description");
        assertThat(savedItem.getAvailable()).isTrue();
    }

    @Test
    @DisplayName("Получение вещи по ID")
    void getItemById_Success() {
        ItemResponseDto savedItem = itemService.createItem(createTestItemDto(), ownerId);
        ItemResponseDto retrievedItem = itemService.getItemById(savedItem.getId());

        assertThat(retrievedItem).isNotNull();
        assertThat(retrievedItem.getId()).isEqualTo(savedItem.getId());
    }

    @Test
    @DisplayName("Получение несуществующей вещи")
    void getItemById_NotFound_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(999));
    }

    @Test
    @DisplayName("Обновление вещи")
    void updateItem_Success() {
        ItemResponseDto savedItem = itemService.createItem(createTestItemDto(), ownerId);

        ItemRequestDto updateDto = ItemRequestDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        ItemResponseDto updatedItem = itemService.updateItem(savedItem.getId(), updateDto, ownerId);

        assertThat(updatedItem).isNotNull();
        assertThat(updatedItem.getName()).isEqualTo("Updated Item");
        assertThat(updatedItem.getDescription()).isEqualTo("Updated Description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Обновление вещи не владельцем")
    void updateItem_NotOwner_ThrowsException() {
        ItemResponseDto savedItem = itemService.createItem(createTestItemDto(), ownerId);

        ItemRequestDto updateDto = ItemRequestDto.builder()
                .name("Updated Item")
                .build();

        assertThrows(Exception.class,
                () -> itemService.updateItem(savedItem.getId(), updateDto, bookerId));
    }

    @Test
    @DisplayName("Удаление вещи")
    void deleteItem_Success() {
        ItemResponseDto savedItem = itemService.createItem(createTestItemDto(), ownerId);
        itemService.deleteItem(savedItem.getId(), ownerId);

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(savedItem.getId()));
    }

    @Test
    @DisplayName("Поиск доступных вещей")
    void searchAvailableItems_Success() {
        // Создаем 2 вещи
        itemService.createItem(createTestItemDto(), ownerId);
        itemService.createItem(createTestItemDto(), ownerId);

        var results = itemService.searchAvailableItems("Item", 1);

        assertThat(results).hasSize(2);
    }

    @Test
    @DisplayName("Поиск с пустым текстом")
    void searchAvailableItems_EmptyText_ReturnsEmptyList() {
        itemService.createItem(createTestItemDto(), ownerId);

        var results = itemService.searchAvailableItems("", 1);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllItemsByOwner_Success() {
        // Создаем 2 вещи
        itemService.createItem(createTestItemDto(), ownerId);
        itemService.createItem(createTestItemDto(), ownerId);

        var items = itemService.getAllItemsByOwner(ownerId);

        assertThat(items).hasSize(2);
    }

    @Test
    @DisplayName("Добавление комментария")
    void addComment_Success() {
        // Создаем бронирование с прошлыми датами
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(LocalDateTime.now().minusDays(5))
                .end(LocalDateTime.now().minusDays(1))
                .build();
        var booking = bookingService.createBooking(bookingDto, bookerId);

        // Подтверждаем бронирование
        bookingService.approveBooking(booking.getId(), true, ownerId);

        // Добавляем комментарий
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        var comment = itemService.addComment(itemId, bookerId, commentDto);

        assertThat(comment).isNotNull();
        assertThat(comment.getText()).isEqualTo("Great item!");
    }

    @Test
    @DisplayName("Добавление комментария без бронирования")
    void addComment_NoBooking_ThrowsException() {
        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        assertThrows(Exception.class,
                () -> itemService.addComment(itemId, bookerId, commentDto));
    }
}