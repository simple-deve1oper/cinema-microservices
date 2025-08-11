package dev.dictionary.country.service;

import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.dictionary.country.entity.Country;

import java.util.List;
import java.util.Set;

/**
 * Интерфейс для описания абстрактных методов сервиса сущности {@link Country}
 */
public interface CountryService {
    /**
     * Получение всех записей о странах
     */
    List<CountryResponse> getAll();

    /**
     * Поиск записи о стране по коду
     * @param code - код страны
     */
    CountryResponse getByCode(String code);

    /**
     * Получение записей стран по переданным кодам
     * @param codes - список кодов
     */
    List<CountryResponse> getAllByCodes(Set<String> codes);

    /**
     * Получение списка кодов стран, которые не принадлежат не одной существующей записи страны
     * @param codes - список кодов
     */
    List<String> getNonExistentCodes(Set<String> codes);
}
