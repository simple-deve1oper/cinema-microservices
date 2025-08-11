package dev.session.service;

import dev.session.entity.Place;
import dev.library.domain.session.dto.PlaceRequest;
import dev.library.domain.session.dto.PlaceResponse;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Place}
 */
public interface PlaceService {
    /**
     * Получение записей всех фильмов
     */
    List<PlaceResponse> getAll();

    /**
     * Получение записей всех мест по идентификатору сеанса
     * @param sessionId - идентификатор сеанса
     */
    List<PlaceResponse> getAllBySession_Id(Long sessionId);

    /**
     * Получение записей мест по переданному списку идентификаторов
     * @param ids - список идентификаторов мест
     */
    List<PlaceResponse> getAllByIds(Set<Long> ids);

    /**
     * Получение записи о месте по идентификатору
     * @param id - идентификатор
     */
    PlaceResponse getById(Long id);

    /**
     * Создание новой записи о сеансе
     * @param request - объект типа {@link PlaceRequest}
     */
    PlaceResponse create(PlaceRequest request);

    /**
     * Обновление существующей записи о месте
     * @param id - идентификатор
     * @param request - объект типа {@link PlaceRequest}
     */
    PlaceResponse update(Long id, PlaceRequest request);

    /**
     * Обновление доступности мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    void updateAvailable(Long sessionId, Set<Long> ids, Boolean available);

    /**
     * Удаление записи о месте по идентификатору
     * @param id - идентификатор
     */
    void deleteById(Long id);

    /**
     * Получение первого идентификатора места, который не равен переданному идентификатору сеанса из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    Long getPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Set<Long> ids);

    /**
     * Получение первого идентификатора места, который равен переданному идентификатору сеанса и доступности из списка идентификаторов мест
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     * @param available - доступность
     */
    Long getPlaceBySessionIdAndIdsAndAvailable(Long sessionId, Set<Long> ids, Boolean available);
}
