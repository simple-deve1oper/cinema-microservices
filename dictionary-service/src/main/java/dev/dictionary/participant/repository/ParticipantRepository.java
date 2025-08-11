package dev.dictionary.participant.repository;

import dev.dictionary.participant.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Репозиторий для сущности {@link Participant}
 */
public interface ParticipantRepository extends JpaRepository<Participant, Long> {
    /**
     * Получение идентификаторов существующих участников фильмов по переданному списку идентификаторов участников фильма
     * @param ids - список идентификаторов
     */
    @Query(value = "SELECT p.id FROM Participant p WHERE p.id IN :ids")
    List<Long> findExistentIds(@Param("ids") Iterable<Long> ids);
}
