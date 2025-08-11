package dev.session.repository;

import dev.session.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для сущности {@link Place}
 */
@Repository
public interface PlaceRepository extends JpaRepository<Place, Long> {
    /**
     * Получение всех записей о места по идентификатору сеанса
     * @param sessionId - идентификатор сеанса
     */
    List<Place> findAllBySession_Id(Long sessionId);

    /**
     * Проверка на существование места по идентификатору сеанса, ряду и номеру
     * @param sessionId - идентификатор места
     * @param row - ряд
     * @param number - номер
     */
    boolean existsBySession_IdAndRowAndNumber(Long sessionId, Integer row, Integer number);

    /**
     * Получение записей о местах по переданному списку идентификатору мест
     * @param ids - список идентификаторов мест
     */
    @Query("SELECT p FROM Place p WHERE p.id IN :ids")
    List<Place> findAllByIds(@Param("ids") Iterable<Long> ids);

    /**
     * Обновление доступности мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    @Modifying
    @Query("UPDATE Place p SET p.available = :available WHERE p.session.id = :sessionId AND p.id IN :ids")
    void updateAvailable(Long sessionId, Iterable<Long> ids, Boolean available);

    /**
     * Получение первого идентификатора места, который не равен переданному идентификатору сеанса из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    @Query(value = "SELECT p.id FROM places p WHERE p.session_id != :sessionId AND p.id IN :ids LIMIT 1", nativeQuery = true)
    Optional<Long> findPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Iterable<Long> ids);

    /**
     * Получение первого идентификатора места, который равен переданному идентификатору сеанса и доступности из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param available - доступность
     * @param ids - список идентификаторов мест
     */
    @Query(value = "SELECT p.id FROM places p WHERE p.available = :available AND session_id = :sessionId AND id IN :ids LIMIT 1", nativeQuery = true)
    Optional<Long> findPlaceBySessionIdAndAvailableAndIds(Long sessionId, Boolean available, Iterable<Long> ids);
}
