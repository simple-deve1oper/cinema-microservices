package dev.receipt.repository;

import dev.receipt.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для сущности {@link Receipt}
 */
@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
    /**
     * Проверка на существование записи о квитанции по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    boolean existsByBookingId(Long bookingId);

    /**
     * Проверка на существование записи о квитанции по идентификатору бронирования и идентификатору пользователя
     * @param bookingId - идентификатор бронирования
     * @param userId - идентификатор пользователя
     */
    boolean existsByBookingIdAndUserId(Long bookingId, String userId);

    /**
     * Получение файла в байтовом представлении по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    @Query("SELECT r.data FROM Receipt r WHERE r.bookingId = :bookingId")
    Optional<byte[]> findDataByBookingId(Long bookingId);

    /**
     * Обновление файла в байтовом представлении по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     * @param data - файл в байтовом представлении
     */
    @Modifying
    @Query("UPDATE Receipt r SET r.data = :data WHERE r.bookingId = :bookingId")
    void updateDataByBookingId(Long bookingId, byte[] data);

    /**
     * Обновление идентификатора пользователя и файла в байтовом представлении по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     * @param userId - идентификатор пользователя
     * @param data - файл в байтовом представлении
     */
    @Query("UPDATE Receipt r SET r.userId = :userId, r.data = :data WHERE r.bookingId = :bookingId")
    @Modifying
    void updateUserIdAndDataByBookingId(Long bookingId, String userId, byte[] data);

    /**
     * Удаление записи о квитанции по идентификатору бронирования
     * @param bookingId - идентификатор бронирования
     */
    void deleteByBookingId(Long bookingId);
}
