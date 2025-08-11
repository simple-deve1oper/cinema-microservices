package dev.dictionary.participant.service;

import dev.library.domain.dictionary.participant.dto.ParticipantResponse;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link dev.dictionary.participant.entity.Participant}
 *
 * @version 1.0
 */
public interface ParticipantService {
    /**
     * Получение всех записей об участниках фильмов
     */
    List<ParticipantResponse> getAll();

    /**
     * Поиск записи об участнике фильма по идентификатору
     * @param id - идентификатор
     */
    ParticipantResponse getById(Long id);

    /**
     * Получение записей участников фильмов по переданным идентификаторам
     * @param ids - список идентификаторов
     */
    List<ParticipantResponse> getAllByIds(Set<Long> ids);

    /**
     * Получение списка идентификаторов участников фильма, которые не принадлежат не одной существующей записи участников фильмов
     * @param ids - список идентификаторов
     */
    List<Long> getNonExistentIds(Set<Long> ids);
}
