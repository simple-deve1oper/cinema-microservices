package dev.session.service;

import dev.library.domain.session.dto.SessionSearchRequest;
import dev.library.domain.session.dto.SessionRequest;
import dev.library.domain.session.dto.SessionResponse;
import dev.session.entity.Session;

import java.util.List;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Session}
 */
public interface SessionService {
    /**
     * Получение записей всех сеансов
     * @param searchRequest - объект типа {@link SessionSearchRequest}
     */
    List<SessionResponse> getAll(SessionSearchRequest searchRequest);

    /**
     * Получение записи о сеансе по идентификатору
     * @param id - идентификатор
     */
    SessionResponse getById(Long id);

    /**
     * Создание новой записи о сеансе
     * @param request - объект типа {@link SessionRequest}
     */
    SessionResponse create(SessionRequest request);

    /**
     * Обновление существующей записи о сеансе
     * @param id - идентификатор
     * @param request - объект типа {@link SessionRequest}
     */
    SessionResponse update(Long id, SessionRequest request);

    /**
     * Удаление записи о сеансе по идентификатору
     * @param id - идентификатор
     */
    void deleteById(Long id);

    /**
     * Получение сущности по идентификатору
     * @param id - идентификатор
     */
    Session findById(Long id);
}
