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
    }

    private ItemRequestDto createTestItemDto(String name, String description) {
        return ItemRequestDto.builder()
                .name(name)
                .description(description)
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Создание вещи")
    void createItem_Success() {
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
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
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        ItemResponseDto savedItem = itemService.createItem(itemDto, ownerId);

        ItemResponseDto retrievedItem = itemService.getItemById(savedItem.getId());

        assertThat(retrievedItem).isNotNull();
        assertThat(retrievedItem.getId()).isEqualTo(savedItem.getId());
        assertThat(retrievedItem.getName()).isEqualTo("Test Item");
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
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        ItemResponseDto savedItem = itemService.createItem(itemDto, ownerId);

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
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        ItemResponseDto savedItem = itemService.createItem(itemDto, ownerId);

        ItemRequestDto updateDto = ItemRequestDto.builder()
                .name("Updated Item")
                .build();

        assertThrows(Exception.class,
                () -> itemService.updateItem(savedItem.getId(), updateDto, bookerId));
    }

    @Test
    @DisplayName("Удаление вещи")
    void deleteItem_Success() {
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        ItemResponseDto savedItem = itemService.createItem(itemDto, ownerId);

        itemService.deleteItem(savedItem.getId(), ownerId);

        assertThrows(NotFoundException.class,
                () -> itemService.getItemById(savedItem.getId()));
    }

    @Test
    @DisplayName("Поиск доступных вещей")
    void searchAvailableItems_Success() {
        // Создаем вещи с уникальными названиями
        itemService.createItem(createTestItemDto("Drill", "Powerful drill"), ownerId);
        itemService.createItem(createTestItemDto("Hammer", "Heavy hammer"), ownerId);
        itemService.createItem(createTestItemDto("Screwdriver", "Small screwdriver"), ownerId);

        // Ищем по слову "Drill" - должна найтись только 1 вещь
        var results = itemService.searchAvailableItems("Drill", 1);
        assertThat(results).hasSize(1);

        // Ищем по слову "Item" - должны найтись все 3
        results = itemService.searchAvailableItems("Item", 1);
        assertThat(results).hasSize(3);
    }

    @Test
    @DisplayName("Поиск с пустым текстом")
    void searchAvailableItems_EmptyText_ReturnsEmptyList() {
        itemService.createItem(createTestItemDto("Test Item", "Test Description"), ownerId);

        var results = itemService.searchAvailableItems("", 1);

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllItemsByOwner_Success() {
        // Создаем 3 вещи для владельца
        itemService.createItem(createTestItemDto("Item 1", "Description 1"), ownerId);
        itemService.createItem(createTestItemDto("Item 2", "Description 2"), ownerId);
        itemService.createItem(createTestItemDto("Item 3", "Description 3"), ownerId);

        var items = itemService.getAllItemsByOwner(ownerId);

        assertThat(items).hasSize(3);
        assertThat(items).allMatch(item -> item.getName().startsWith("Item"));
    }

    @Test
    @DisplayName("Добавление комментария")
    void addComment_Success() {
        // Создаем вещь
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        Integer itemId = itemService.createItem(itemDto, ownerId).getId();

        // Создаем бронирование с прошлыми датами
        LocalDateTime now = LocalDateTime.now();
        BookingRequestDto bookingDto = BookingRequestDto.builder()
                .itemId(itemId)
                .start(now.minusDays(5))
                .end(now.minusDays(1))
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
        assertThat(comment.getAuthorName()).isEqualTo("Booker");
    }

    @Test
    @DisplayName("Добавление комментария без бронирования")
    void addComment_NoBooking_ThrowsException() {
        // Создаем вещь
        ItemRequestDto itemDto = createTestItemDto("Test Item", "Test Description");
        Integer itemId = itemService.createItem(itemDto, ownerId).getId();

        CommentRequestDto commentDto = new CommentRequestDto();
        commentDto.setText("Great item!");

        assertThrows(Exception.class,
                () -> itemService.addComment(itemId, bookerId, commentDto));
    }
}