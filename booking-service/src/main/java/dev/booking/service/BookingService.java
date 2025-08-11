package dev.booking.service;

import dev.booking.entity.Booking;
import dev.library.domain.booking.dto.BookingRequest;
import dev.library.domain.booking.dto.BookingResponse;
import dev.library.domain.booking.dto.BookingSearchRequest;
import dev.library.domain.booking.dto.BookingStatusRequest;
import dev.library.domain.rabbitmq.ActionType;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Booking}
 */
public interface BookingService {
    /**
     * Получение всех записей о бронированиях
     * @param searchDto - объект типа {@link BookingSearchRequest}
     */
    List<Booking> getAll(BookingSearchRequest searchDto);

    /**
     * Получение записи о бронировании по идентификатору
     * @param id - идентификатор
     */
    Booking getById(Long id);

    /**
     * Получение записи о бронировании по идентификатору и идентификатору пользователя
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    Booking getById(Long id, String userId);

    /**
     * Проверка на существовании записи по идентификатору и идентификатору пользователя
     * @param id - идентификатор
     * @param userId - идентификатор пользователя
     */
    boolean existsByUserId(Long id, String userId);

    /**
     * Создание новой записи о бронировании
     * @param bookingRequest - объект типа {@link BookingRequest}
     */
    BookingResponse create(BookingRequest bookingRequest);

    /**
     * Обновление существующей записи о бронировании
     * @param id - идентификатор
     * @param bookingRequest - объект типа {@link BookingRequest}
     */
    BookingResponse update(Long id, BookingRequest bookingRequest);

    /**
     * Обновление статуса и у существующей записи о бронировании
     * @param id - идентификатор
     * @param request - объект типа {@link BookingStatusRequest}
     */
    BookingResponse updateStatus(Long id, BookingStatusRequest request);

    /**
     * Удаление записи о бронировании
     * @param id - идентификатор
     */
    void deleteById(Long id);

    /**
     * Отправка сообщения в брокер сообщений о создании или обновлении записи по бронированию
     * @param bookingResponse - объект типа {@link BookingResponse}
     * @param type - перечисление типа {@link ActionType}
     */
    void sendMessage(BookingResponse bookingResponse, ActionType type);

    /**
     * Отправка сообщения в брокер сообщений об удалении записи по бронированию
     * @param id - идентификатор бронирования
     * @param userId - идентификатор пользователя
     */
    void sendMessage(Long id, String userId);

    /**
     * Получение объекта типа {@link BookingResponse}
     * @param booking - объект типа {@link Booking}
     */
    BookingResponse buildResponse(Booking booking);
}
