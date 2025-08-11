package dev.movie.service.impl;

import dev.library.core.exception.BadRequestException;
import dev.library.domain.dictionary.country.client.CountryClient;
import dev.library.domain.dictionary.country.dto.CountryResponse;
import dev.movie.entity.Movie;
import dev.movie.entity.MovieCountry;
import dev.movie.mapper.MovieCountryMapper;
import dev.movie.repository.MovieCountryRepository;
import dev.movie.service.MovieCountryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис, реализующий интерфейс {@link MovieCountryService}
 */
@Service
@RequiredArgsConstructor
public class MovieCountryServiceImpl implements MovieCountryService {
    private final MovieCountryRepository repository;
    private final MovieCountryMapper mapper;
    private final CountryClient client;

    @Value("${errors.country.codes.not-found}")
    private String countryCodesNotFound;

    @Override
    @Transactional
    public List<MovieCountry> create(Movie movie, Set<String> countryCodes) {
        checkNonExistentCodes(countryCodes);

        return createEntities(movie, countryCodes);
    }

    @Override
    @Transactional
    public void update(Movie movie, Set<String> countryCodes) {
        checkNonExistentCodes(countryCodes);
        List<String> currentCountryCodes = movie.getCountries().stream().map(MovieCountry::getCountryCode).toList();
        Set<String> countryCodesForRemove = getCodesForRemove(currentCountryCodes, countryCodes);
        if (!countryCodesForRemove.isEmpty()) {
            movie.getCountries().removeIf(country -> countryCodesForRemove
                    .contains(country.getCountryCode()));
        }
        Set<String> countryCodesForCreate = getCodesForCreate(currentCountryCodes, countryCodes);
        if (!countryCodesForCreate.isEmpty()) {
            List<MovieCountry> countries = createEntities(movie, countryCodesForCreate);
            movie.getCountries().addAll(countries);
        }
    }

    @Override
    public List<CountryResponse> getCountryResponsesByMovieId(List<MovieCountry> countries) {
        return client
                .getAllByCodes(countries.stream().map(MovieCountry::getCountryCode).collect(Collectors.toSet()));
    }

    /**
     * Проверка списка кодов стран на не существующие кода стран
     * @param countryCodes - список кодов стран
     */
    private void checkNonExistentCodes(Set<String> countryCodes) {
        List<String> nonExistentCodes = client.getNonExistentCodes(countryCodes);
        if (!nonExistentCodes.isEmpty()) {
            String errorMessage = countryCodesNotFound
                    .formatted(nonExistentCodes);
            throw new BadRequestException(errorMessage);
        }
    }

    /**
     * Получение списка идентификаторов для удаления
     * @param currentCountryCodes - текущий список кодов
     * @param countryCodes - новый список кодов
     */
    private Set<String> getCodesForRemove(List<String> currentCountryCodes, Set<String> countryCodes) {
        return currentCountryCodes.stream()
                .filter(countryCode -> !countryCodes.contains(countryCode))
                .collect(Collectors.toSet());
    }

    /**
     * Получение списка идентификаторов для создания
     * @param currentCountryCodes - текущий список кодов
     * @param countryCodes - новый список кодов
     */
    private Set<String> getCodesForCreate(List<String> currentCountryCodes, Set<String> countryCodes) {
        return countryCodes.stream()
                .filter(countryCode -> !currentCountryCodes.contains(countryCode))
                .collect(Collectors.toSet());
    }

    /**
     * Создание новых записей о странах фильмов
     * @param movie - объект типа {@link Movie}
     * @param countryCodes - список кодов стран
     */
    private List<MovieCountry> createEntities(Movie movie, Set<String> countryCodes) {
        List<MovieCountry> countries = countryCodes.stream().map(code -> mapper.toEntity(movie, code)).toList();

        return repository.saveAll(countries);
    }
}