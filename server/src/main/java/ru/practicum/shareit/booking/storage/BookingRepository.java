package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingResponseStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId")
    Optional<Booking> findByIdWithBookerAndItem(@Param("bookingId") Integer bookingId);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.booker.id = :bookerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByBookerIdOrderByStartDesc(@Param("bookerId") Integer bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE i.owner.id = :ownerId " +
            "ORDER BY b.start DESC")
    List<Booking> findByItemOwnerIdOrderByStartDesc(@Param("ownerId") Integer ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end < :end " +
            "ORDER BY b.end DESC")
    List<Booking> findByItemIdAndStatusAndEndBeforeOrderByEndDesc(
            @Param("itemId") Integer itemId,
            @Param("status") BookingResponseStatus status,
            @Param("end") LocalDateTime end,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start > :start " +
            "ORDER BY b.start ASC")
    List<Booking> findByItemIdAndStatusAndStartAfterOrderByStartAsc(
            @Param("itemId") Integer itemId,
            @Param("status") BookingResponseStatus status,
            @Param("start") LocalDateTime start,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :bookerId " +
            "AND b.status = :status " +
            "AND b.end < :currentTime")
    List<Booking> findBookingsForComment(
            @Param("itemId") Integer itemId,
            @Param("bookerId") Integer bookerId,
            @Param("status") BookingResponseStatus status,
            @Param("currentTime") LocalDateTime currentTime);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = 'APPROVED' " +
            "AND ((b.start BETWEEN :start AND :end) " +
            "OR (b.end BETWEEN :start AND :end) " +
            "OR (b.start <= :start AND b.end >= :end))")
    boolean existsApprovedBookingForItem(@Param("itemId") Integer itemId,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end);

    @Query("SELECT b FROM Booking b " +
            "JOIN FETCH b.booker " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH i.owner " +
            "WHERE b.id = :bookingId " +
            "AND (b.booker.id = :userId OR i.owner.id = :userId)")
    Optional<Booking> findByIdAndUserId(@Param("bookingId") Integer bookingId,
                                        @Param("userId") Integer userId);


    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.end < :currentTime " +
            "ORDER BY b.end DESC")
    List<Booking> findLastBookingForItem(
            @Param("itemId") Integer itemId,
            @Param("status") BookingResponseStatus status,
            @Param("currentTime") LocalDateTime currentTime,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.status = :status " +
            "AND b.start > :currentTime " +
            "ORDER BY b.start ASC")
    List<Booking> findNextBookingForItem(
            @Param("itemId") Integer itemId,
            @Param("status") BookingResponseStatus status,
            @Param("currentTime") LocalDateTime currentTime,
            Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByItemId(@Param("itemId") Integer itemId);
}