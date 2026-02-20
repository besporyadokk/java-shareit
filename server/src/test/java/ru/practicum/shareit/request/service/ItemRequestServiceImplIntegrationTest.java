package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestServiceImplIntegrationTest {

    @Autowired
    private ItemRequestServiceImpl itemRequestService;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private Integer requesterId;

    @BeforeEach
    void setUp() {
        itemRequestRepository.deleteAll();

        UserResponseDto requester = new UserResponseDto();
        requester.setName("Requester");
        requester.setEmail("requester@example.com");
        requesterId = userService.create(requester).getId();
    }

    private ItemRequestCreateDto createTestRequestDto() {
        return ItemRequestCreateDto.builder()
                .description("Need a drill")
                .build();
    }

    @Test
    @DisplayName("Создание запроса на вещь")
    void createItemRequest_Success() {
        ItemRequestCreateDto requestDto = createTestRequestDto();
        ItemRequestDto savedRequest = itemRequestService.createItemRequest(requestDto, requesterId);

        assertThat(savedRequest).isNotNull();
        assertThat(savedRequest.getId()).isNotNull();
        assertThat(savedRequest.getDescription()).isEqualTo("Need a drill");
    }

    @Test
    @DisplayName("Получение своих запросов")
    void getOwnItemRequests_Success() {
        itemRequestService.createItemRequest(createTestRequestDto(), requesterId);
        itemRequestService.createItemRequest(createTestRequestDto(), requesterId);

        var requests = itemRequestService.getOwnItemRequests(requesterId);

        assertThat(requests).hasSize(2);
    }

    @Test
    @DisplayName("Получение запроса по ID")
    void getItemRequestById_Success() {
        ItemRequestCreateDto requestDto = createTestRequestDto();
        ItemRequestDto savedRequest = itemRequestService.createItemRequest(requestDto, requesterId);

        ItemRequestDto retrievedRequest = itemRequestService.getItemRequestById(savedRequest.getId(), requesterId);

        assertThat(retrievedRequest).isNotNull();
        assertThat(retrievedRequest.getId()).isEqualTo(savedRequest.getId());
    }

    @Test
    @DisplayName("Получение несуществующего запроса")
    void getItemRequestById_NotFound_ThrowsException() {
        assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(999, requesterId));
    }

    @Test
    @DisplayName("Получение всех запросов других пользователей")
    void getAllItemRequests_Success() {
        itemRequestService.createItemRequest(createTestRequestDto(), requesterId);

        // Создаем другого пользователя с запросом
        UserResponseDto otherUser = new UserResponseDto();
        otherUser.setName("Other");
        otherUser.setEmail("other@example.com");
        Integer otherId = userService.create(otherUser).getId();
        itemRequestService.createItemRequest(createTestRequestDto(), otherId);

        var requests = itemRequestService.getAllItemRequests(requesterId, 0, 10);

        assertThat(requests).hasSize(1); // Должен видеть только запрос другого пользователя
    }
}