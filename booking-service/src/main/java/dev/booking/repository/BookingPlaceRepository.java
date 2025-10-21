package dev.booking.repository;

import dev.booking.entity.BookingPlace;
import dev.library.domain.booking.dto.constant.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для сущности {@link BookingPlace}
 */
@Repository
public interface BookingPlaceRepository extends JpaRepository<BookingPlace, Long> {
    /**
     * Получение записей о местах для бронирования по идентификатору сеанса и статусу бронирования
     * @param sessionId - идентификатор сеанса
     * @param bookingStatus - статус бронирования
     */
    @Query("SELECT bp from BookingPlace bp join Booking b on bp.booking.id = b.id WHERE b.sessionId = :sessionId and b.bookingStatus = :bookingStatus")
    List<BookingPlace> findByBooking_SessionIdAndBooking_BookingStatus(Long sessionId, BookingStatus bookingStatus);
}
