package dev.dictionary.country.mapper;

import dev.dictionary.country.entity.Country;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import org.springframework.stereotype.Component;

/**
 * Класс для преобразования данных типа {@link Country}
 */
@Component
public class CountryMapper {
    /**
     * Преобразование данных из {@link Country} в {@link CountryResponse}
     * @param country - объект типа Country
     */
    public CountryResponse toResponse(Country country) {
        return new CountryResponse(
                country.getId(),
                country.getCode(),
                country.getName()
        );
    }
}
