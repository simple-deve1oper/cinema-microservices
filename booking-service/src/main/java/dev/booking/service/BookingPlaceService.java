package dev.booking.service;

import dev.booking.entity.Booking;
import dev.booking.entity.BookingPlace;
import dev.library.domain.booking.dto.constant.BookingStatus;
import dev.library.domain.session.dto.PlaceResponse;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link BookingPlace}
 */
public interface BookingPlaceService {
    /**
     * Получение списка объектов типа {@link PlaceResponse}
     * @param placeIds - идентификаторы мест
     */
    List<PlaceResponse> getPlaceResponses(Set<Long> placeIds);

    /**
     * Получение занятого номера места у сеанса
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    long getPlaceBySessionIdAndIdsAndAvailableFalse(Long sessionId, Set<Long> ids);

    /**
     * Получение места, который не принадлежит сеансу
     * @param sessionId - идентификатор сеанса
     * @param ids - список идентификаторов мест
     */
    long getPlaceNotEqualsSessionBySessionIdAndIds(Long sessionId, Set<Long> ids);

    /**
     * Создание мест для бронирования
     * @param sessionId - идентификатор сеанса
     * @param booking - объект типа {@link Booking}
     * @param placeIds - список идентификаторов мест
     */
    List<BookingPlace> create(Long sessionId, Booking booking, Set<Long> placeIds);

    /**
     * Обновление мест для бронирования
     * @param oldSessionId - идентификатор сеанса текущей записи
     * @param booking - объект типа {@link Booking}
     * @param placeIdsForRemove - список идентификаторов мест для удаления
     * @param placeIdsForCreate - список идентификаторов мест для создания
     */
    void update(Long oldSessionId, Booking booking, Set<Long> placeIdsForRemove,
                              Set<Long> placeIdsForCreate);

    /**
     * Обновление доступности мест
     * @param sessionId - идентификатор сеанса
     * @param placeIds - список идентификаторов мест
     * @param available - доступность
     */
    void updateAvailability(Long sessionId, Set<Long> placeIds, Boolean available);

    /**
     * Получение идентификаторов мест для удаления
     * @param currentPlaceIds - текущий список идентификаторов мест
     * @param placeIds - новый список идентификаторов мест
     */
    Set<Long> getIdsForRemove(Set<Long> currentPlaceIds, Set<Long> placeIds);

    /**
     * Получение идентификаторов мест для создания
     * @param currentPlaceIds - текущий список идентификаторов мест
     * @param placeIds - новый список идентификаторов мест
     */
    Set<Long> getIdsForCreate(Set<Long> currentPlaceIds, Set<Long> placeIds);

    /**
     * Получение записей о местах для бронирования по идентификатору сеанса и статусу бронирования
     * @param sessionId - идентификатор сеанса
     * @param bookingStatus - статус бронирования
     */
    List<BookingPlace> findByBooking_SessionIdAndBooking_BookingStatus(Long sessionId, BookingStatus bookingStatus);
}
