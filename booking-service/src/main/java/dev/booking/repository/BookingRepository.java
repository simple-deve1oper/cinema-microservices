package dev.booking.repository;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.constant.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link Booking}
 */
public interface BookingRepository extends JpaRepository<Booking, Long>, JpaSpecificationExecutor<Booking> {
    /**
     * Получение бронирования по идентификатору и идентификатору пользователя
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    Optional<Booking> findByIdAndUserId(Long id, String userId);

    /**
     * Проверка на существование бронирования по идентификатору и статусу
     * @param id - идентификатор
     * @param bookingStatus - статус бронирования
     */
    boolean existsByIdAndBookingStatus(Long id, BookingStatus bookingStatus);
}
