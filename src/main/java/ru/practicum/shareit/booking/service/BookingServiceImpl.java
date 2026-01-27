package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto, Integer bookerId) {
        User booker = userRepository.findById(bookerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingRequestDto.getStart();
        LocalDateTime end = bookingRequestDto.getEnd();

        // Валидация дат
        if (start.isBefore(now)) {
            log.warn("Попытка создать бронирование с началом в прошлом: {}", start);
            throw new BadRequestException("Начало бронирования не может быть в прошлом");
        }

        if (!end.isAfter(start)) {
            log.warn("Дата окончания {} не после даты начала {}", end, start);
            throw new BadRequestException("Дата окончания должна быть после даты начала");
        }

        // Проверка, что владелец не бронирует свою вещь
        if (item.getOwner().getId().equals(bookerId)) {
            log.warn("Владелец с id {} пытается забронировать свою вещь с id {}", bookerId, item.getId());
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        // Проверка доступности вещи
        if (!item.getAvailable()) {
            log.warn("Вещь с id {} недоступна для бронирования", item.getId());
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        // Проверка пересечения с другими бронированиями
        if (bookingRepository.existsApprovedBookingForItem(item.getId(), start, end)) {
            log.warn("Вещь с id {} уже забронирована на указанные даты", item.getId());
            throw new BadRequestException("Вещь уже забронирована на указанные даты");
        }

        Booking booking = BookingMapper.toBooking(bookingRequestDto, item, booker);
        Booking savedBooking = bookingRepository.save(booking);

        log.info("Создан booking: {}", savedBooking);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Integer bookingId, Boolean approved, Integer ownerId) {
        Booking booking = bookingRepository.findByIdWithBookerAndItem(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            log.warn("Пользователь с id {} не является владельцем вещи для бронирования с id {}",
                    ownerId, bookingId);
            throw new AccessDeniedException("Подтверждать может только владелец вещи");
        }

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            log.warn("Попытка изменить статус уже обработанного бронирования с id {}, текущий статус: {}",
                    bookingId, booking.getStatus());
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        if (!approved) {
            Item item = booking.getItem();
            item.setAvailable(true);
            itemRepository.save(item);
            log.info("Вещь с id {} снова доступна для бронирования после отклонения бронирования с id {}",
                    item.getId(), bookingId);
        }

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking {} обновлён. Статус: {}", updatedBooking.getId(), updatedBooking.getStatus());

        return BookingMapper.toBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено или доступ запрещен"));

        log.info("Возвращено бронирование с id {} для пользователя с id {}", bookingId, userId);
        return BookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getUserBookings(Integer userId, BookingState state, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;

        bookings = bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable);

        List<Booking> filteredBookings = filterByState(bookings, state);

        log.info("Найдено {} бронирований для пользователя с id {}, состояние: {}",
                filteredBookings.size(), userId, state);

        return filteredBookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getOwnerBookings(Integer ownerId, BookingState state, Integer from, Integer size) {
        userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));


        if (itemRepository.findByOwnerId(ownerId).isEmpty()) {
            throw new BadRequestException("У пользователя нет вещей для бронирования");
        }

        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Direction.DESC, "start"));
        List<Booking> bookings;

        bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(ownerId, pageable);


        List<Booking> filteredBookings = filterByState(bookings, state);

        log.info("Найдено {} бронирований для владельца с id {}, состояние: {}",
                filteredBookings.size(), ownerId, state);

        return filteredBookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    private List<Booking> filterByState(List<Booking> bookings, BookingState state) {
        LocalDateTime now = LocalDateTime.now();

        return bookings.stream()
                .filter(booking -> {
                    switch (state) {
                        case CURRENT:
                            return booking.getStart().isBefore(now) && booking.getEnd().isAfter(now);
                        case PAST:
                            return booking.getEnd().isBefore(now);
                        case FUTURE:
                            return booking.getStart().isAfter(now);
                        case WAITING:
                            return booking.getStatus() == BookingStatus.WAITING;
                        case REJECTED:
                            return booking.getStatus() == BookingStatus.REJECTED;
                        case ALL:
                        default:
                            return true;
                    }
                })
                .collect(Collectors.toList());
    }
}