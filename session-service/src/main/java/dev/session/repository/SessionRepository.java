package dev.session.repository;

import dev.session.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.OffsetDateTime;

/**
 * Репозиторий для сущности {@link Session}
 */
public interface SessionRepository extends JpaRepository<Session, Long>, JpaSpecificationExecutor<Session> {
    /**
     * Проверка на существование сеанса по залу и дате с временем
     * @param hall - зал
     * @param dateTime - дата и время
     */
    boolean existsByHallAndDateTime(Integer hall, OffsetDateTime dateTime);
}
