package dev.dictionary.country.repository;

import dev.dictionary.country.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для сущности {@link Country}
 */
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
    /**
     * Получение записи о стране по коду
     * @param code - код страны
     */
    Optional<Country> findByCode(String code);

    /**
     * Получение записей стран по переданным кодам
     * @param code - список кодов
     */
    List<Country> findAllByCodeIn(Collection<String> code);

    /**
     * Получение кодов существующих стран по переданному списку кодов стран
     * @param codes - список кодов
     */
    @Query(value = "SELECT c.code FROM Country c WHERE c.code IN :codes")
    List<String> findExistentCodes(@Param("codes") Iterable<String> codes);
}
