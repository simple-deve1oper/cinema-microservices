package dev.booking.repository;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.constant.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

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

    /**
     * Обновление статуса бронирований по списку идентификаторов бронирований
     * @param ids - список идентификаторов бронирований
     * @param bookingStatus - статус бронирования
     */
    @Modifying
    @Query("UPDATE Booking b SET b.bookingStatus = :bookingStatus WHERE b.id IN :ids")
    void updateStatus(Set<Long> ids, BookingStatus bookingStatus);

    /**
     * Проверка на существование бронирований по идентификатору сеанса
     * @param sessionId - идентификатор сеанса
     */
    boolean existsBySessionId(Long sessionId);
}
